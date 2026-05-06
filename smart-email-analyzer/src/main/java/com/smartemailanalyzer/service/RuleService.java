package com.smartemailanalyzer.service;

import com.smartemailanalyzer.dto.KeywordRuleForm;
import com.smartemailanalyzer.dto.SenderRuleForm;
import com.smartemailanalyzer.entity.KeywordRule;
import com.smartemailanalyzer.entity.ScoreWeight;
import com.smartemailanalyzer.entity.SenderRule;
import com.smartemailanalyzer.entity.User;
import com.smartemailanalyzer.repository.KeywordRuleRepository;
import com.smartemailanalyzer.repository.ScoreWeightRepository;
import com.smartemailanalyzer.repository.SenderRuleRepository;
import com.smartemailanalyzer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * User-configurable rules and weights.
 */
@Service
public class RuleService {

    public static final String WEIGHT_URGENT = "WEIGHT_URGENT";
    public static final String WEIGHT_PAYMENT = "WEIGHT_PAYMENT";
    public static final String WEIGHT_MEETING = "WEIGHT_MEETING";
    public static final String WEIGHT_PROMOTION = "WEIGHT_PROMOTION";
    public static final String WEIGHT_IMPORTANT_SENDER = "WEIGHT_IMPORTANT_SENDER";
    public static final String WEIGHT_RECENT = "WEIGHT_RECENT";
    public static final String WEIGHT_SUBJECT_PRIORITY = "WEIGHT_SUBJECT_PRIORITY";

    private final KeywordRuleRepository keywordRuleRepository;
    private final SenderRuleRepository senderRuleRepository;
    private final ScoreWeightRepository scoreWeightRepository;
    private final UserRepository userRepository;

    public RuleService(KeywordRuleRepository keywordRuleRepository,
                       SenderRuleRepository senderRuleRepository,
                       ScoreWeightRepository scoreWeightRepository,
                       UserRepository userRepository) {
        this.keywordRuleRepository = keywordRuleRepository;
        this.senderRuleRepository = senderRuleRepository;
        this.scoreWeightRepository = scoreWeightRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Integer> defaultWeights() {
        Map<String, Integer> defaults = new LinkedHashMap<>();
        defaults.put(WEIGHT_URGENT, 30);
        defaults.put(WEIGHT_PAYMENT, 22);
        defaults.put(WEIGHT_MEETING, 18);
        defaults.put(WEIGHT_PROMOTION, 10);
        defaults.put(WEIGHT_IMPORTANT_SENDER, 20);
        defaults.put(WEIGHT_RECENT, 5);
        defaults.put(WEIGHT_SUBJECT_PRIORITY, 8);
        return defaults;
    }

    @Transactional
    public void initializeDefaultsForUser(User user) {
        defaultWeights().forEach((key, value) -> {
            Optional<ScoreWeight> existing = scoreWeightRepository.findByUserIdAndKey(user.getId(), key);
            if (existing.isEmpty()) {
                ScoreWeight scoreWeight = new ScoreWeight();
                scoreWeight.setUser(user);
                scoreWeight.setWeightKey(key);
                scoreWeight.setWeightValue(value);
                scoreWeightRepository.save(scoreWeight);
            }
        });
    }

    @Transactional(readOnly = true)
    public List<KeywordRule> getKeywordRules(Long userId) {
        return keywordRuleRepository.findByUserId(userId);
    }

    @Transactional
    public void addKeywordRule(Long userId, KeywordRuleForm form) {
        User user = requireUser(userId);
        KeywordRule rule = new KeywordRule();
        rule.setUser(user);
        rule.setKeyword(form.getKeyword().trim().toLowerCase(Locale.ROOT));
        rule.setTag(form.getTag().trim().toUpperCase(Locale.ROOT));
        rule.setScoreBoost(form.getScoreBoost() == null ? 0 : form.getScoreBoost());
        keywordRuleRepository.save(rule);
    }

    @Transactional
    public void deleteKeywordRule(Long userId, Long ruleId) {
        keywordRuleRepository.findByIdAndUserId(ruleId, userId)
                .ifPresent(keywordRuleRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<SenderRule> getSenderRules(Long userId) {
        return senderRuleRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<SenderRule> findSenderRule(Long userId, String senderEmail) {
        if (senderEmail == null || senderEmail.isBlank()) {
            return Optional.empty();
        }
        return senderRuleRepository.findByUserIdAndSenderEmail(userId, senderEmail.trim().toLowerCase(Locale.ROOT));
    }

    @Transactional
    public void addOrUpdateSenderRule(Long userId, SenderRuleForm form) {
        User user = requireUser(userId);
        String normalizedSenderEmail = form.getSenderEmail().trim().toLowerCase(Locale.ROOT);
        SenderRule senderRule = senderRuleRepository.findByUserIdAndSenderEmail(userId, normalizedSenderEmail)
                .orElseGet(SenderRule::new);
        senderRule.setUser(user);
        senderRule.setSenderEmail(normalizedSenderEmail);
        senderRule.setScoreBoost(form.getScoreBoost() == null ? 0 : form.getScoreBoost());
        senderRuleRepository.save(senderRule);
    }

    @Transactional
    public void deleteSenderRule(Long userId, Long ruleId) {
        senderRuleRepository.findByIdAndUserId(ruleId, userId)
                .ifPresent(senderRuleRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<ScoreWeight> getScoreWeights(Long userId) {
        return scoreWeightRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getWeightMap(Long userId) {
        Map<String, Integer> weights = defaultWeights();
        for (ScoreWeight scoreWeight : scoreWeightRepository.findByUserId(userId)) {
            weights.put(scoreWeight.getWeightKey(), scoreWeight.getWeightValue());
        }
        return weights;
    }

    @Transactional
    public void updateWeights(Long userId, Map<String, String> submittedValues) {
        User user = requireUser(userId);
        Map<String, Integer> defaults = defaultWeights();
        for (Map.Entry<String, Integer> entry : defaults.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            if (submittedValues.containsKey(key)) {
                try {
                    value = Integer.parseInt(submittedValues.get(key));
                } catch (NumberFormatException ex) {
                    value = entry.getValue();
                }
            }

            ScoreWeight scoreWeight = scoreWeightRepository.findByUserIdAndKey(userId, key)
                    .orElseGet(ScoreWeight::new);
            scoreWeight.setUser(user);
            scoreWeight.setWeightKey(key);
            scoreWeight.setWeightValue(value);
            scoreWeightRepository.save(scoreWeight);
        }
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }
}
