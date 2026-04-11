package com.emailanalyzer.service;

import com.emailanalyzer.entity.Email;
import java.util.List;
import java.util.Map;

public interface EmailService {

    Email findById(Long id);

    List<Email> getInboxForUser(Long userId);

    List<Email> filterByTag(Long userId, String tagName);

    List<Email> searchBySubject(Long userId, String keyword);

    List<Email> getSortedByScore(Long userId);

    /** Update urgency score manually (sets manual_override = true) */
    void updateScore(Long emailId, int newScore);

    /** Reply to an email via SMTP */
    void replyToEmail(Long emailId, String replyBody, Long userId);

    // ── Dashboard stats ──────────────────────────────────────────

    int countTodayEmails(Long userId);

    int countUrgentEmails(Long userId);

    /** Returns tag-name → count map */
    Map<String, Long> getTagFrequency(Long userId);

    /** Returns sender → count of URGENT emails */
    Map<String, Long> getHighRiskSenders(Long userId);
}
