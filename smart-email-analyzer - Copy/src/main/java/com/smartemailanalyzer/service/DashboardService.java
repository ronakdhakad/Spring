package com.smartemailanalyzer.service;

import com.smartemailanalyzer.dto.DashboardStats;
import com.smartemailanalyzer.entity.EmailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds dashboard summary information.
 */
@Service
public class DashboardService {

    private final EmailService emailService;

    public DashboardService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats(Long userId) {
        DashboardStats stats = new DashboardStats();
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        stats.setTotalEmailsToday(emailService.countToday(userId, start, end));
        stats.setTotalUrgentEmails(emailService.countUrgent(userId));
        stats.setMostCommonTag(resolveMostCommonTag(userId));
        return stats;
    }

    private String resolveMostCommonTag(Long userId) {
        Map<String, Integer> tagCounter = new HashMap<>();
        for (EmailMessage emailMessage : emailService.getAllByUserId(userId)) {
            for (String tag : emailMessage.getTagList()) {
                tagCounter.put(tag, tagCounter.getOrDefault(tag, 0) + 1);
            }
        }
        return tagCounter.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}
