package com.smartemailanalyzer.service;

import com.smartemailanalyzer.dto.EmailAnalysisResult;
import com.smartemailanalyzer.entity.KeywordRule;
import com.smartemailanalyzer.entity.SenderRule;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Applies built-in and user-defined rules to produce tags and urgency score.
 */
@Service
public class RuleEngineService {

    private static final String[] URGENT_KEYWORDS = {
            "urgent", "asap", "immediately", "important", "action required", "deadline", "rescheduled"
    };

    private static final String[] PAYMENT_KEYWORDS = {
            "payment", "invoice", "paid", "receipt", "transaction", "due", "billing"
    };

    private static final String[] PROMOTION_KEYWORDS = {
            "offer", "sale", "discount", "promotion", "promo", "coupon", "deal"
    };

    private static final String[] MEETING_KEYWORDS = {
            "meeting", "interview", "schedule", "scheduled", "call", "appointment", "calendar"
    };

    private final RuleService ruleService;

    public RuleEngineService(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    public EmailAnalysisResult analyzeEmail(Long userId,
                                            String subject,
                                            String senderEmail,
                                            String cleanedContent,
                                            LocalDateTime receivedTime) {
        Map<String, Integer> weights = ruleService.getWeightMap(userId);
        List<KeywordRule> keywordRules = ruleService.getKeywordRules(userId);
        Optional<SenderRule> senderRule = ruleService.findSenderRule(userId, senderEmail);

        String safeSubject = safeLower(subject);
        String safeBody = safeLower(cleanedContent);
        String combined = safeSubject + "\n" + safeBody;

        Set<String> tags = new LinkedHashSet<>();
        int score = 0;

        if (containsAny(combined, URGENT_KEYWORDS)) {
            tags.add("URGENT");
            score += weights.getOrDefault(RuleService.WEIGHT_URGENT, 30);
        }
        if (containsAny(combined, PAYMENT_KEYWORDS)) {
            tags.add("PAYMENT");
            score += weights.getOrDefault(RuleService.WEIGHT_PAYMENT, 22);
        }
        if (containsAny(combined, MEETING_KEYWORDS)) {
            tags.add("MEETING");
            score += weights.getOrDefault(RuleService.WEIGHT_MEETING, 18);
        }
        if (containsAny(combined, PROMOTION_KEYWORDS)) {
            tags.add("PROMOTION");
            score += weights.getOrDefault(RuleService.WEIGHT_PROMOTION, 10);
        }

        if (safeSubject.contains("urgent") || safeSubject.contains("asap") || safeSubject.contains("!")) {
            score += weights.getOrDefault(RuleService.WEIGHT_SUBJECT_PRIORITY, 8);
        }

        if (receivedTime != null && receivedTime.isAfter(LocalDateTime.now().minusHours(6))) {
            score += weights.getOrDefault(RuleService.WEIGHT_RECENT, 5);
        }

        if (senderRule.isPresent()) {
            score += senderRule.get().getScoreBoost();
            tags.add("IMPORTANT");
        }

        for (KeywordRule rule : keywordRules) {
            if (containsKeyword(combined, rule.getKeyword())) {
                tags.add(rule.getTag().toUpperCase(Locale.ROOT));
                score += rule.getScoreBoost();
            }
        }

        if (tags.isEmpty()) {
            tags.add("GENERAL");
        }

        score = Math.max(0, Math.min(100, score));
        return new EmailAnalysisResult(cleanedContent, new ArrayList<>(tags), score);
    }

    private boolean containsAny(String content, String[] keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsKeyword(String content, String keyword) {
        return keyword != null && !keyword.isBlank() && content.contains(keyword.toLowerCase(Locale.ROOT));
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
