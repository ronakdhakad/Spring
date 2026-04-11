package com.emailanalyzer.util;

import com.emailanalyzer.entity.Email;
import com.emailanalyzer.entity.User;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Connects to an IMAP server and fetches recent emails for a user.
 *
 * For Gmail:
 *   - Enable IMAP in Gmail Settings → Forwarding and POP/IMAP
 *   - Use an App Password (Google Account → Security → App Passwords)
 *   - Set imapHost = imap.gmail.com, imapPort = 993
 */
@Component
public class ImapEmailFetcher {

    private static final Logger log = LoggerFactory.getLogger(ImapEmailFetcher.class);

    /** Maximum number of recent emails to fetch per run (newest first) */
    private static final int FETCH_LIMIT = 50;

    /**
     * Fetches recent emails for the given user via IMAP.
     *
     * @param user The user whose IMAP credentials will be used
     * @return List of Email entities (NOT yet persisted, caller must save)
     */
    public List<Email> fetchEmails(User user) {

        List<Email> fetched = new ArrayList<>();

        if (user.getImapPassword() == null || user.getImapPassword().isBlank()) {
            log.warn("User {} has no IMAP password configured; skipping fetch.", user.getEmail());
            return fetched;
        }

        Properties props = buildImapProperties(user.getImapHost(), user.getImapPort());
        Session session  = Session.getInstance(props);

        try (Store store = session.getStore("imaps")) {

            log.info("Connecting to IMAP for user: {}", user.getEmail());
            store.connect(
                user.getImapHost(),
                user.getImapPort(),
                user.getEmail(),
                user.getImapPassword()
            );

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            // Calculate start index to get last FETCH_LIMIT messages
            int start = Math.max(1, totalMessages - FETCH_LIMIT + 1);

            Message[] messages = inbox.getMessages(start, totalMessages);

            // Iterate newest-first by reversing
            for (int i = messages.length - 1; i >= 0; i--) {
                try {
                    Email email = convertToEntity(messages[i], user);
                    if (email != null) {
                        fetched.add(email);
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse message {}: {}", i, e.getMessage());
                }
            }

            inbox.close(false);
            log.info("Fetched {} emails for user {}", fetched.size(), user.getEmail());

        } catch (AuthenticationFailedException e) {
            log.error("IMAP authentication failed for {}: {}. Check App Password.", user.getEmail(), e.getMessage());
        } catch (MessagingException e) {
            log.error("IMAP connection error for {}: {}", user.getEmail(), e.getMessage());
        }

        return fetched;
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────

    private Properties buildImapProperties(String host, int port) {
        Properties props = new Properties();
        props.put("mail.store.protocol",      "imaps");
        props.put("mail.imaps.host",          host);
        props.put("mail.imaps.port",          String.valueOf(port));
        props.put("mail.imaps.ssl.enable",    "true");
        props.put("mail.imaps.timeout",       "15000");
        props.put("mail.imaps.connectiontimeout", "15000");
        return props;
    }

    /**
     * Converts a Jakarta Mail {@link Message} into an {@link Email} entity.
     */
    private Email convertToEntity(Message message, User user) throws MessagingException, IOException {

        Email email = new Email();
        email.setUser(user);

        // ── Message-ID (for deduplication) ──────────────────────────
        String[] messageIdHeaders = message.getHeader("Message-ID");
        if (messageIdHeaders != null && messageIdHeaders.length > 0) {
            email.setMessageId(messageIdHeaders[0].trim());
        } else {
            // Fallback: build a pseudo-id from date + subject
            email.setMessageId("pseudo-" + message.getSentDate() + "-" + message.getSubject());
        }

        // ── Sender ───────────────────────────────────────────────────
        Address[] from = message.getFrom();
        if (from != null && from.length > 0) {
            if (from[0] instanceof InternetAddress ia) {
                email.setSender(ia.getAddress());
            } else {
                email.setSender(from[0].toString());
            }
        }

        // ── Subject ──────────────────────────────────────────────────
        String subject = message.getSubject();
        email.setSubject(subject != null ? subject : "(no subject)");

        // ── Received Time ────────────────────────────────────────────
        Date receivedDate = message.getReceivedDate();
        if (receivedDate == null) receivedDate = message.getSentDate();
        if (receivedDate != null) {
            email.setReceivedTime(
                receivedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            );
        } else {
            email.setReceivedTime(LocalDateTime.now());
        }

        // ── Body (extract plain text) ────────────────────────────────
        String body = extractTextBody(message);
     // Body ko safely truncate karo — max 65000 characters
        if (body != null && body.length() > 65000) {
            body = body.substring(0, 65000) + "\n\n[... content truncated ...]";
        }
        email.setBody(body);

        return email;
    }

    /**
     * Recursively extracts plain-text content from a possibly multipart message.
     */
    private String extractTextBody(Part part) throws MessagingException, IOException {

        if (part.isMimeType("text/plain")) {
            return (String) part.getContent();
        }

        if (part.isMimeType("text/html")) {
            // Strip HTML tags for a clean plain-text view
            String html = (String) part.getContent();
            return html.replaceAll("<[^>]+>", " ")
                       .replaceAll("\\s+", " ")
                       .trim();
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            // Prefer text/plain parts; fall back to html
            String htmlFallback = null;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return extractTextBody(bodyPart);
                }
                if (bodyPart.isMimeType("text/html") && htmlFallback == null) {
                    htmlFallback = extractTextBody(bodyPart);
                }
                if (bodyPart.isMimeType("multipart/*")) {
                    String nested = extractTextBody(bodyPart);
                    if (nested != null && !nested.isBlank()) return nested;
                }
            }
            if (htmlFallback != null) return htmlFallback;
        }

        return "(no readable content)";
    }
}
