package com.mailsense.service;

import com.mailsense.entity.Email;
import com.mailsense.entity.UserEmailConfig;
import com.mailsense.util.EmailParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class ImapFetcherService {

    public List<Email> fetchNewEmails(UserEmailConfig config, String plainPass) {
        List<Email> result = new ArrayList<>();
        Store store   = null;
        Folder inbox  = null;

        try {
            Properties props = new Properties();
            props.put("mail.store.protocol",          "imaps");
            props.put("mail.imaps.host",              config.getImapHost());
            props.put("mail.imaps.port",              String.valueOf(config.getImapPort()));
            props.put("mail.imaps.ssl.enable",        "true");
            props.put("mail.imaps.connectiontimeout", "10000");
            props.put("mail.imaps.timeout",           "10000");
            props.put("mail.imaps.ssl.trust",         config.getImapHost());

            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect(config.getImapHost(), config.getImapPort(),
                          config.getEmailAddress(), plainPass);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.search(
                new javax.mail.search.FlagTerm(new Flags(Flags.Flag.SEEN), false));

            log.info("Found {} unseen messages for {}", messages.length, config.getEmailAddress());
            int limit = Math.min(messages.length, 50);

            for (int i = 0; i < limit; i++) {
                try {
                    Email email = convert(messages[i]);
                    if (email != null) result.add(email);
                } catch (Exception e) {
                    log.warn("Failed to parse message {}: {}", i, e.getMessage());
                }
            }

        } catch (AuthenticationFailedException e) {
            log.error("IMAP auth failed for {}", config.getEmailAddress());
            throw new RuntimeException("IMAP auth failed. Check App Password.", e);
        } catch (MessagingException e) {
            log.error("IMAP error for {}: {}", config.getEmailAddress(), e.getMessage());
            throw new RuntimeException("IMAP connection failed", e);
        } finally {
            try { if (inbox != null && inbox.isOpen()) inbox.close(false); } catch (Exception ignored) {}
            try { if (store != null && store.isConnected()) store.close();  } catch (Exception ignored) {}
        }
        return result;
    }

    private Email convert(Message msg) throws Exception {
        String[] msgIds = msg.getHeader("Message-ID");
        String messageId = (msgIds != null && msgIds.length > 0)
            ? msgIds[0].trim() : UUID.randomUUID().toString();

        String from    = msg.getFrom() != null && msg.getFrom().length > 0
                         ? msg.getFrom()[0].toString() : "unknown";
        String subject = msg.getSubject() != null ? msg.getSubject() : "(no subject)";

        String rawBody = extractBody(msg);
        EmailParserUtil.ParsedEmail parsed = EmailParserUtil.parse(rawBody);

        LocalDateTime receivedAt = msg.getReceivedDate() != null
            ? msg.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            : LocalDateTime.now();

        boolean hasAttachment = msg.getContent() instanceof Multipart;

        return Email.builder()
            .messageId(messageId)
            .fromAddress(from)
            .subject(subject)
            .fullBody(parsed.fullBody)
            .bodyPreview(parsed.preview)
            .hasAttachment(hasAttachment)
            .receivedAt(receivedAt)
            .read(false)
            .urgencyScore(20)
            .urgencyLevel(Email.UrgencyLevel.LOW)
            .build();
    }

    private String extractBody(Message msg) throws Exception {
        Object content = msg.getContent();
        if (content instanceof String) return (String) content;
        if (content instanceof MimeMultipart) return fromMultipart((MimeMultipart) content);
        return "";
    }

    private String fromMultipart(MimeMultipart mp) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mp.getCount(); i++) {
            BodyPart part = mp.getBodyPart(i);
            String ct = part.getContentType().toLowerCase();
            if (ct.startsWith("text/html")) { sb.append(part.getContent()); break; }
            else if (ct.startsWith("text/plain")) sb.append(part.getContent());
            else if (part.getContent() instanceof MimeMultipart)
                sb.append(fromMultipart((MimeMultipart) part.getContent()));
        }
        return sb.toString();
    }
}
