package com.emailanalyzer.util;

import com.emailanalyzer.entity.Email;
import com.emailanalyzer.entity.Rule;
import com.emailanalyzer.entity.Tag;
import com.emailanalyzer.dao.TagDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Core Rule Engine.
 *
 * Given an Email and a list of Rules, computes:
 *   - Urgency score (sum of matched rule scores, clamped 0-100)
 *   - Set of Tags to assign
 *   - Human-readable score breakdown string
 */
@Component
public class RuleEngine {

    private static final Logger log = LoggerFactory.getLogger(RuleEngine.class);

    /** Maximum allowed score */
    private static final int MAX_SCORE = 100;

    @Autowired
    private TagDAO tagDAO;

    /**
     * Analyse an email against the given rules.
     *
     * @param email  The email to analyse (subject + body + sender are read)
     * @param rules  All active rules from DB
     * @return       Result object with score, tags, and breakdown text
     */
    public AnalysisResult analyse(Email email, List<Rule> rules) {

        int totalScore = 0;
        Set<Tag> assignedTags = new HashSet<>();
        StringBuilder breakdown = new StringBuilder();

        String subjectLower = email.getSubject() != null
                ? email.getSubject().toLowerCase() : "";
        String bodyLower    = email.getBody() != null
                ? email.getBody().toLowerCase()    : "";
        String senderLower  = email.getSender() != null
                ? email.getSender().toLowerCase()  : "";

        // Combined text for keyword matching (subject + body)
        String fullText = subjectLower + " " + bodyLower;

        for (Rule rule : rules) {

            if (Rule.TYPE_KEYWORD.equalsIgnoreCase(rule.getType())) {
                // ── KEYWORD RULE ──────────────────────────────────────────────
                String keyword = rule.getValue() != null
                        ? rule.getValue().toLowerCase() : "";
                if (!keyword.isEmpty() && fullText.contains(keyword)) {
                    int contribution = rule.getScore() != null ? rule.getScore() : 0;
                    totalScore += contribution;
                    String sign = contribution >= 0 ? "+" : "";
                    breakdown.append("• Keyword \"")
                             .append(rule.getValue())
                             .append("\" matched (")
                             .append(sign).append(contribution)
                             .append(")\n");
                    assignTag(rule.getTag(), assignedTags);
                    log.debug("Keyword rule matched: '{}' +{}", rule.getValue(), contribution);
                }

            } else if (Rule.TYPE_SENDER.equalsIgnoreCase(rule.getType())) {
                // ── SENDER RULE ───────────────────────────────────────────────
                String senderRule = rule.getValue() != null
                        ? rule.getValue().toLowerCase() : "";
                if (!senderRule.isEmpty() && senderLower.contains(senderRule)) {
                    int contribution = rule.getScore() != null ? rule.getScore() : 0;
                    totalScore += contribution;
                    String sign = contribution >= 0 ? "+" : "";
                    breakdown.append("• Important sender \"")
                             .append(rule.getValue())
                             .append("\" (")
                             .append(sign).append(contribution)
                             .append(")\n");
                    assignTag(rule.getTag(), assignedTags);
                    log.debug("Sender rule matched: '{}' +{}", rule.getValue(), contribution);
                }
            }
        }

        // Clamp score to valid range [0, MAX_SCORE]
        totalScore = Math.max(0, Math.min(MAX_SCORE, totalScore));

        // Add score summary at top of breakdown
        String finalBreakdown = "Urgency Score: " + totalScore + "\nReasons:\n"
                + (breakdown.length() > 0 ? breakdown.toString() : "• No rules matched\n");

        log.info("Email '{}' scored {} with {} tags", email.getSubject(), totalScore, assignedTags.size());

        return new AnalysisResult(totalScore, assignedTags, finalBreakdown);
    }

    /** Look up the Tag entity by name and add it to the set */
    private void assignTag(String tagName, Set<Tag> assignedTags) {
        if (tagName == null || tagName.isBlank()) return;
        try {
            Tag tag = tagDAO.findByName(tagName.trim());
            if (tag == null) {
                // Auto-create tag if not in DB
                tag = new Tag(tagName.trim().toUpperCase());
                tagDAO.save(tag);
            }
            assignedTags.add(tag);
        } catch (Exception e) {
            log.warn("Could not assign tag '{}': {}", tagName, e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Inner class: Analysis Result
    // ──────────────────────────────────────────────────────────────────────────

    public static class AnalysisResult {

        private final int      score;
        private final Set<Tag> tags;
        private final String   breakdown;

        public AnalysisResult(int score, Set<Tag> tags, String breakdown) {
            this.score     = score;
            this.tags      = tags;
            this.breakdown = breakdown;
        }

        public int      getScore()     { return score; }
        public Set<Tag> getTags()      { return tags; }
        public String   getBreakdown() { return breakdown; }
    }
}
