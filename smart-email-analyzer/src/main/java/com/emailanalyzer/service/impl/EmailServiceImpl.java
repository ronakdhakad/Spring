package com.emailanalyzer.service.impl;

import com.emailanalyzer.dao.EmailDAO;
import com.emailanalyzer.dao.UrgencyScoreDAO;
import com.emailanalyzer.dao.UserDAO;
import com.emailanalyzer.entity.*;
import com.emailanalyzer.service.EmailService;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired private EmailDAO        emailDAO;
    @Autowired private UrgencyScoreDAO urgencyScoreDAO;
    @Autowired private UserDAO         userDAO;

    // ──────────────────────────────────────────────────────────────
    // READ operations
    // ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Email findById(Long id) {
        return emailDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> getInboxForUser(Long userId) {
        return emailDAO.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> filterByTag(Long userId, String tagName) {
        if (tagName == null || tagName.isBlank()) {
            return emailDAO.findByUserId(userId);
        }
        return emailDAO.findByUserIdAndTag(userId, tagName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> searchBySubject(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return emailDAO.findByUserId(userId);
        }
        return emailDAO.searchBySubject(userId, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> getSortedByScore(Long userId) {
        return emailDAO.findByUserIdSortedByScore(userId);
    }

    // ──────────────────────────────────────────────────────────────
    // WRITE operations
    // ──────────────────────────────────────────────────────────────

    @Override
    public void updateScore(Long emailId, int newScore) {
        UrgencyScore us = urgencyScoreDAO.findByEmailId(emailId);
        if (us == null) {
            // Create score record if it didn't exist
            Email email = emailDAO.findById(emailId);
            if (email == null) throw new IllegalArgumentException("Email not found: " + emailId);
            us = new UrgencyScore();
            us.setEmail(email);
            us.setEmailId(emailId);
            us.setBreakdown("Manually set by user.");
            urgencyScoreDAO.save(us);
        }
        us.setScore(Math.max(0, Math.min(100, newScore)));
        us.setManualOverride(true);
        urgencyScoreDAO.update(us);
        log.info("Score for email {} manually updated to {}", emailId, newScore);
    }

    // ──────────────────────────────────────────────────────────────
    // REPLY via SMTP
    // ──────────────────────────────────────────────────────────────

    @Override
    public void replyToEmail(Long emailId, String replyBody, Long userId) {
        Email original = emailDAO.findById(emailId);
        if (original == null) throw new IllegalArgumentException("Email not found: " + emailId);

        User user = userDAO.findById(userId);
        if (user == null) throw new IllegalArgumentException("User not found: " + userId);
        if (user.getImapPassword() == null || user.getImapPassword().isBlank()) {
            throw new IllegalStateException("SMTP password not configured for user.");
        }

        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth",            "true");
        smtpProps.put("mail.smtp.starttls.enable", "true");
        smtpProps.put("mail.smtp.host",            "smtp.gmail.com");
        smtpProps.put("mail.smtp.port",            "587");
        smtpProps.put("mail.smtp.ssl.trust",       "smtp.gmail.com");

        final String fromEmail    = user.getEmail();
        final String fromPassword = user.getImapPassword();

        Session session = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        try {
            MimeMessage reply = new MimeMessage(session);
            reply.setFrom(new InternetAddress(fromEmail));
            reply.setRecipient(Message.RecipientType.TO,
                new InternetAddress(original.getSender()));
            reply.setSubject("Re: " + original.getSubject());
            reply.setText(replyBody + "\n\n--- Original Message ---\n" + original.getBody());
            Transport.send(reply);
            log.info("Reply sent to {} from {}", original.getSender(), fromEmail);
        } catch (MessagingException e) {
            log.error("Failed to send reply: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send reply: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // DASHBOARD stats
    // ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public int countTodayEmails(Long userId) {
        return emailDAO.findTodayByUserId(userId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public int countUrgentEmails(Long userId) {
        return (int) emailDAO.findByUserIdAndTag(userId, "URGENT").size();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTagFrequency(Long userId) {
        List<Email> emails = emailDAO.findByUserId(userId);
        Map<String, Long> freq = new LinkedHashMap<>();
        for (Email email : emails) {
            for (Tag tag : email.getTags()) {
                freq.merge(tag.getName(), 1L, Long::sum);
            }
        }
        // Sort by count descending
        return freq.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getHighRiskSenders(Long userId) {
        List<Email> urgentEmails = emailDAO.findByUserIdAndTag(userId, "URGENT");
        Map<String, Long> senderCount = new LinkedHashMap<>();
        for (Email e : urgentEmails) {
            if (e.getSender() != null) {
                senderCount.merge(e.getSender(), 1L, Long::sum);
            }
        }
        return senderCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }
}
