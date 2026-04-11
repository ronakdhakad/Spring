package com.emailanalyzer.scheduler;

import com.emailanalyzer.dao.EmailDAO;
import com.emailanalyzer.dao.RuleDAO;
import com.emailanalyzer.dao.UrgencyScoreDAO;
import com.emailanalyzer.dao.UserDAO;
import com.emailanalyzer.entity.*;
import com.emailanalyzer.util.ImapEmailFetcher;
import com.emailanalyzer.util.RuleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Scheduled job that:
 *  1. Fetches emails from IMAP for every registered user
 *  2. Skips duplicates (Message-ID check)
 *  3. Runs Rule Engine to compute urgency score and assign tags
 *  4. Persists new emails + scores to DB
 *
 * Runs every 5 minutes (300,000 ms).
 */
@Component
public class EmailFetchScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmailFetchScheduler.class);

    @Autowired private UserDAO          userDAO;
    @Autowired private EmailDAO         emailDAO;
    @Autowired private RuleDAO          ruleDAO;
    @Autowired private UrgencyScoreDAO  urgencyScoreDAO;
    @Autowired private ImapEmailFetcher imapFetcher;
    @Autowired private RuleEngine       ruleEngine;

    /**
     * Main scheduled task.
     * fixedRate = 300000 ms = 5 minutes.
     * initialDelay = 10000 ms = wait 10 s after app startup before first run.
     */
    @Scheduled(fixedRate = 300000, initialDelay = 10000)
    @Transactional
    public void fetchAndProcessEmails() {

        log.info("=== Email Fetch Scheduler started ===");

        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            log.info("No users in DB; skipping.");
            return;
        }

        // Load all rules once (shared across all users)
        List<Rule> rules = ruleDAO.findAll();
        log.info("Loaded {} rules", rules.size());

        for (User user : users) {
            processUser(user, rules);
        }

        log.info("=== Email Fetch Scheduler completed ===");
    }

    // ─────────────────────────────────────────────────────────────
    // Per-user processing
    // ─────────────────────────────────────────────────────────────

    private void processUser(User user, List<Rule> rules) {
        log.info("Processing user: {}", user.getEmail());

        List<Email> fetchedEmails;
        try {
            fetchedEmails = imapFetcher.fetchEmails(user);
        } catch (Exception e) {
            log.error("Failed to fetch emails for {}: {}", user.getEmail(), e.getMessage());
            return;
        }

        int newCount = 0;
        for (Email email : fetchedEmails) {
            // Har email ke liye ALAG transaction — ek fail ho toh baaki save hoti rahein
            try {
                processSingleEmail(email, rules);
                newCount++;
            } catch (Exception e) {
                log.error("Error processing email '{}': {}", email.getSubject(), e.getMessage());
                // Agle email pe chale jao — transaction rollback hua hai
            }
        }

        log.info("User {}: {} new emails saved.", user.getEmail(), newCount);
    }

    // Naya method — har email ke liye alag @Transactional
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void processSingleEmail(Email email, List<Rule> rules) {
        // Deduplication check
        if (emailDAO.existsByMessageId(email.getMessageId())) {
            log.debug("Skipping duplicate: {}", email.getMessageId());
            return;
        }

        // Body truncate karo
        String body = email.getBody();
        if (body != null && body.length() > 65000) {
            body = body.substring(0, 65000) + "\n\n[... truncated ...]";
            email.setBody(body);
        }

        // Save email
        emailDAO.save(email);

        // Rule Engine
        RuleEngine.AnalysisResult result = ruleEngine.analyse(email, rules);
        email.setTags(result.getTags());
        emailDAO.update(email);

        // Save score
        UrgencyScore score = new UrgencyScore(email, result.getScore(), result.getBreakdown());
        urgencyScoreDAO.save(score);

        log.debug("Saved email '{}' with score {}", email.getSubject(), result.getScore());
    
    }
}
