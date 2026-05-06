package com.smartemailanalyzer.service;

import com.smartemailanalyzer.entity.EmailMessage;
import com.smartemailanalyzer.repository.EmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Email persistence and query logic.
 */
@Service
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Transactional
    public EmailMessage saveParsedEmail(EmailMessage emailMessage) {
        return emailRepository.save(emailMessage);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserAndMessageUid(Long userId, String messageUid) {
        return emailRepository.existsByUserIdAndMessageUid(userId, messageUid);
    }

    @Transactional(readOnly = true)
    public List<EmailMessage> getInboxEmails(Long userId) {
        return emailRepository.findInboxByUserId(userId);
    }

    @Transactional(readOnly = true)
    public EmailMessage getEmailDetail(Long emailId, Long userId) {
        return emailRepository.findByIdAndUserId(emailId, userId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<EmailMessage> getFilteredEmails(Long userId, String tag, String sort) {
        return emailRepository.findFiltered(userId, tag, sort);
    }

    @Transactional(readOnly = true)
    public Set<String> getAvailableTags(Long userId) {
        Set<String> tags = new TreeSet<>();
        for (EmailMessage emailMessage : emailRepository.findAllByUserId(userId)) {
            tags.addAll(emailMessage.getTagList());
        }
        return tags;
    }

    @Transactional(readOnly = true)
    public List<EmailMessage> getAllByUserId(Long userId) {
        return emailRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countToday(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return emailRepository.countTodayByUserId(userId, start, end);
    }

    @Transactional(readOnly = true)
    public long countUrgent(Long userId) {
        return emailRepository.findUrgentEmails(userId).size();
    }
}
