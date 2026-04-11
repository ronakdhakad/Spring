package com.emailanalyzer.dao;

import com.emailanalyzer.entity.Email;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object for Email entity.
 */
public interface EmailDAO {

    void save(Email email);

    void update(Email email);

    Email findById(Long id);

    /** Check if an email with this IMAP message-id already exists (dedup) */
    boolean existsByMessageId(String messageId);

    /** All emails for a given user, newest first */
    List<Email> findByUserId(Long userId);

    /** Emails for user filtered by tag name */
    List<Email> findByUserIdAndTag(Long userId, String tagName);

    /** Full-text search on subject for a user */
    List<Email> searchBySubject(Long userId, String keyword);

    /** All emails for user received today */
    List<Email> findTodayByUserId(Long userId);

    /** Emails sorted by urgency score descending */
    List<Email> findByUserIdSortedByScore(Long userId);

    /** All emails for all users (used by scheduler) */
    List<Email> findAll();

    void delete(Long id);
}
