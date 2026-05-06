package com.smartemailanalyzer.service;

import com.smartemailanalyzer.dto.EmailAnalysisResult;
import com.smartemailanalyzer.entity.EmailMessage;
import com.smartemailanalyzer.entity.User;
import jakarta.mail.Address;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Fetches emails from Gmail via IMAP and stores analyzed results.
 */
@Service
public class EmailFetchService {

    private final UserService userService;
    private final EmailService emailService;
    private final MailContentService mailContentService;
    private final RuleEngineService ruleEngineService;

    @Value("${mail.imap.host}")
    private String mailHost;

    @Value("${mail.imap.port}")
    private int mailPort;

    @Value("${mail.imap.protocol}")
    private String mailProtocol;

    @Value("${mail.imap.folder}")
    private String mailFolder;

    @Value("${mail.imap.connectionTimeout}")
    private String connectionTimeout;

    @Value("${mail.imap.timeout}")
    private String timeout;

    @Value("${mail.initial.fetch.count}")
    private int initialFetchCount;

    public EmailFetchService(UserService userService,
                             EmailService emailService,
                             MailContentService mailContentService,
                             RuleEngineService ruleEngineService) {
        this.userService = userService;
        this.emailService = emailService;
        this.mailContentService = mailContentService;
        this.ruleEngineService = ruleEngineService;
    }

    /**
     * Scheduler that syncs inboxes for currently logged-in users every 5 minutes.
     */
    @Scheduled(fixedDelayString = "${mail.scheduler.fixedDelayMillis}")
    public void fetchForLoggedInUsers() {
        for (User user : userService.getLoggedInUsers()) {
            try {
                fetchNewEmailsForUser(user.getId());
            } catch (Exception ex) {
                System.err.println("Failed to sync emails for user " + user.getEmail() + ": " + ex.getMessage());
            }
        }
    }

    public int fetchNewEmailsForUser(Long userId) throws MessagingException, IOException {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Properties properties = buildMailProperties();
        Session session = Session.getInstance(properties);
        Store store = null;
        Folder folder = null;
        int savedCount = 0;
        Long maxUidSeen = user.getImapLastUid();

        try {
            store = session.getStore(mailProtocol);
            store.connect(mailHost, mailPort, user.getEmail(), user.getGoogleAppPassword());
            folder = store.getFolder(mailFolder);
            folder.open(Folder.READ_ONLY);

            if (folder.getMessageCount() == 0) {
                return 0;
            }

            List<MessageHolder> messageHolders = resolveMessagesToProcess(folder, user.getImapLastUid());
            for (MessageHolder holder : messageHolders) {
                Message message = holder.message();
                String messageUid = resolveMessageUid(message, holder.uid());
                if (emailService.existsByUserAndMessageUid(user.getId(), messageUid)) {
                    maxUidSeen = resolveMaxUid(maxUidSeen, holder.uid());
                    continue;
                }

                String cleanedContent = mailContentService.extractText(message);
                String subject = message.getSubject() == null || message.getSubject().isBlank()
                        ? "(No Subject)"
                        : message.getSubject();
                String senderText = extractSenderText(message);
                String senderEmail = extractSenderEmail(message);
                LocalDateTime receivedTime = toLocalDateTime(resolveReceivedDate(message));

                EmailAnalysisResult analysis = ruleEngineService.analyzeEmail(
                        user.getId(),
                        subject,
                        senderEmail,
                        cleanedContent,
                        receivedTime
                );

                EmailMessage emailMessage = new EmailMessage();
                emailMessage.setUser(user);
                emailMessage.setMessageUid(messageUid);
                emailMessage.setSender(senderText);
                emailMessage.setSenderEmail(senderEmail);
                emailMessage.setSubject(subject);
                emailMessage.setReceivedTime(receivedTime);
                emailMessage.setRawContent(cleanedContent);
                emailMessage.setCleanedContent(analysis.getCleanedContent());
                emailMessage.setTagsCsv(analysis.getTagsCsv());
                emailMessage.setUrgencyScore(analysis.getUrgencyScore());
                emailService.saveParsedEmail(emailMessage);
                savedCount++;

                maxUidSeen = resolveMaxUid(maxUidSeen, holder.uid());
            }

            if (maxUidSeen != null) {
                userService.updateImapLastUid(user.getId(), maxUidSeen);
            }
        } finally {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
        }

        return savedCount;
    }

    private Properties buildMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", mailProtocol);
        properties.put("mail.imaps.ssl.enable", "true");
        properties.put("mail.imap.connectiontimeout", connectionTimeout);
        properties.put("mail.imap.timeout", timeout);
        properties.put("mail.imaps.connectiontimeout", connectionTimeout);
        properties.put("mail.imaps.timeout", timeout);
        return properties;
    }

    private List<MessageHolder> resolveMessagesToProcess(Folder folder, Long lastUid) throws MessagingException {
        List<MessageHolder> holders = new ArrayList<>();
        if (folder instanceof UIDFolder uidFolder) {
            Message[] messages;
            if (lastUid == null || lastUid <= 0) {
                int totalMessages = folder.getMessageCount();
                int startIndex = Math.max(1, totalMessages - initialFetchCount + 1);
                messages = folder.getMessages(startIndex, totalMessages);
            } else {
                messages = uidFolder.getMessagesByUID(lastUid + 1, UIDFolder.LASTUID);
            }

            if (messages != null) {
                for (Message message : messages) {
                    if (message != null) {
                        holders.add(new MessageHolder(message, uidFolder.getUID(message)));
                    }
                }
            }
        } else {
            int totalMessages = folder.getMessageCount();
            int startIndex = Math.max(1, totalMessages - initialFetchCount + 1);
            Message[] messages = folder.getMessages(startIndex, totalMessages);
            for (Message message : messages) {
                holders.add(new MessageHolder(message, -1L));
            }
        }

        holders.sort(Comparator.comparingLong(MessageHolder::uid));
        return holders;
    }

    private String extractSenderText(Message message) throws MessagingException {
        Address[] addresses = message.getFrom();
        if (addresses == null || addresses.length == 0) {
            return "Unknown Sender";
        }
        Address first = addresses[0];
        if (first instanceof InternetAddress internetAddress) {
            String personal = internetAddress.getPersonal();
            if (personal != null && !personal.isBlank()) {
                return personal + " <" + internetAddress.getAddress() + ">";
            }
            return internetAddress.getAddress();
        }
        return first.toString();
    }

    private String extractSenderEmail(Message message) throws MessagingException {
        Address[] addresses = message.getFrom();
        if (addresses == null || addresses.length == 0) {
            return "unknown@unknown";
        }
        Address first = addresses[0];
        if (first instanceof InternetAddress internetAddress) {
            return internetAddress.getAddress() == null ? "unknown@unknown" : internetAddress.getAddress().toLowerCase();
        }
        return first.toString().toLowerCase();
    }

    private Date resolveReceivedDate(Message message) throws MessagingException {
        Date receivedDate = message.getReceivedDate();
        if (receivedDate != null) {
            return receivedDate;
        }
        Date sentDate = message.getSentDate();
        return sentDate != null ? sentDate : new Date();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String resolveMessageUid(Message message, long uid) throws MessagingException {
        if (uid > 0) {
            return String.valueOf(uid);
        }
        String[] messageIdHeader = message.getHeader("Message-ID");
        if (messageIdHeader != null && messageIdHeader.length > 0) {
            return messageIdHeader[0];
        }
        return message.getMessageNumber() + "-" + Objects.toString(message.getSubject(), "no-subject");
    }

    private Long resolveMaxUid(Long currentMax, long candidateUid) {
        if (candidateUid <= 0) {
            return currentMax;
        }
        if (currentMax == null) {
            return candidateUid;
        }
        return Math.max(currentMax, candidateUid);
    }

    private record MessageHolder(Message message, long uid) {
    }
}
