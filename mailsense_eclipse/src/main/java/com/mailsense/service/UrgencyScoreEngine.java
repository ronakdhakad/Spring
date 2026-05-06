package com.mailsense.service;

import com.mailsense.dao.impl.SenderPrefDaoImpl;
import com.mailsense.entity.Email;
import com.mailsense.entity.SenderPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UrgencyScoreEngine {

    @Autowired
    private SenderPrefDaoImpl senderPrefDao;

    private static final List<String> URGENT_KW   = Arrays.asList("urgent","asap","immediately","critical","emergency","action required");
    private static final List<String> DEADLINE_KW = Arrays.asList("today","by eod","by cob","due today","deadline","by end of day");
    private static final List<String> PAYMENT_KW  = Arrays.asList("invoice","payment due","overdue","outstanding balance","final notice");
    private static final List<String> MEETING_KW  = Arrays.asList("meeting","interview","call today","join now","zoom","google meet");

    @Transactional(readOnly = true)
    public void calculateAndApply(Email email, Long userId) {
        int score = 20;
        String text = buildText(email);

        if (containsAny(text, URGENT_KW))   score += 30;
        if (containsAny(text, DEADLINE_KW)) score += 15;
        if (containsAny(text, PAYMENT_KW))  score += 15;
        if (containsAny(text, MEETING_KW))  score += 10;
        if (email.isHasAttachment())         score += 10;
        score += senderModifier(email.getFromAddress(), userId);

        score = Math.min(100, Math.max(0, score));
        email.setUrgencyScore(score);
        email.setUrgencyLevel(toLevel(score));
        log.debug("Score {} -> {} for [{}]", score, email.getUrgencyLevel(), email.getSubject());
    }

    private String buildText(Email email) {
        return (nvl(email.getSubject()) + " " +
                nvl(email.getBodyPreview()) + " " +
                nvl(email.getFromAddress())).toLowerCase();
    }

    private boolean containsAny(String text, List<String> kws) {
        return kws.stream().anyMatch(text::contains);
    }

    private int senderModifier(String from, Long userId) {
        if (from == null || userId == null) return 0;
        String email = from.contains("<") && from.contains(">")
            ? from.substring(from.indexOf('<') + 1, from.indexOf('>')).trim()
            : from;
        Optional<SenderPreference> pref = senderPrefDao.findByUserIdAndEmail(userId, email);
        return pref.map(SenderPreference::getScoreModifier).orElse(0);
    }

    private Email.UrgencyLevel toLevel(int score) {
        if (score >= 80) return Email.UrgencyLevel.CRITICAL;
        if (score >= 50) return Email.UrgencyLevel.HIGH;
        if (score >= 30) return Email.UrgencyLevel.MEDIUM;
        return Email.UrgencyLevel.LOW;
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
