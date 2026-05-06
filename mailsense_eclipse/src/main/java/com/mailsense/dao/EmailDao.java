package com.mailsense.dao;
import com.mailsense.entity.Email;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface EmailDao {
    Email save(Email email);
    Optional<Email> findById(Long id, Long userId);
    boolean existsByUserAndMessageId(Long userId, String messageId);
    List<Email> findByUserId(Long userId, int page, int pageSize);
    List<Email> findByUserIdAndTag(Long userId, String tag, int page, int pageSize);
    List<Email> findSnoozedDue(LocalDateTime now);
    long countByUserIdAndUrgencyLevel(Long userId, Email.UrgencyLevel level);
    long countUnreadByUserId(Long userId);
    List<Object[]> findTopSendersByUserId(Long userId, int limit);
    Email update(Email email);
    void delete(Long id, Long userId);
}
