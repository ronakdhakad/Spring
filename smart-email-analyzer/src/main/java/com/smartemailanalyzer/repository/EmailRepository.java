package com.smartemailanalyzer.repository;

import com.smartemailanalyzer.entity.EmailMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data access for parsed emails.
 */
@Repository
public class EmailRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EmailMessage save(EmailMessage emailMessage) {
        if (emailMessage.getId() == null) {
            entityManager.persist(emailMessage);
            return emailMessage;
        }
        return entityManager.merge(emailMessage);
    }

    public boolean existsByUserIdAndMessageUid(Long userId, String messageUid) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM EmailMessage e WHERE e.user.id = :userId AND e.messageUid = :messageUid",
                        Long.class)
                .setParameter("userId", userId)
                .setParameter("messageUid", messageUid)
                .getSingleResult();
        return count != null && count > 0;
    }

    public List<EmailMessage> findInboxByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT e FROM EmailMessage e WHERE e.user.id = :userId ORDER BY e.receivedTime DESC",
                        EmailMessage.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<EmailMessage> findByIdAndUserId(Long emailId, Long userId) {
        List<EmailMessage> result = entityManager.createQuery(
                        "SELECT e FROM EmailMessage e WHERE e.id = :emailId AND e.user.id = :userId",
                        EmailMessage.class)
                .setParameter("emailId", emailId)
                .setParameter("userId", userId)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<EmailMessage> findFiltered(Long userId, String tag, String sort) {
        StringBuilder jpql = new StringBuilder("SELECT e FROM EmailMessage e WHERE e.user.id = :userId");
        boolean hasTag = tag != null && !tag.isBlank();
        if (hasTag) {
            jpql.append(" AND LOWER(e.tagsCsv) LIKE :tagPattern");
        }
        if ("scoreLow".equalsIgnoreCase(sort)) {
            jpql.append(" ORDER BY e.urgencyScore ASC, e.receivedTime DESC");
        } else if ("newest".equalsIgnoreCase(sort)) {
            jpql.append(" ORDER BY e.receivedTime DESC");
        } else {
            jpql.append(" ORDER BY e.urgencyScore DESC, e.receivedTime DESC");
        }

        var query = entityManager.createQuery(jpql.toString(), EmailMessage.class)
                .setParameter("userId", userId);
        if (hasTag) {
            query.setParameter("tagPattern", "%" + tag.toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public long countTodayByUserId(Long userId, LocalDateTime start, LocalDateTime end) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM EmailMessage e WHERE e.user.id = :userId " +
                                "AND e.receivedTime >= :start AND e.receivedTime < :end",
                        Long.class)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return count == null ? 0 : count;
    }

    public List<EmailMessage> findUrgentEmails(Long userId) {
        return entityManager.createQuery(
                        "SELECT e FROM EmailMessage e WHERE e.user.id = :userId AND LOWER(e.tagsCsv) LIKE '%urgent%'",
                        EmailMessage.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<EmailMessage> findAllByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT e FROM EmailMessage e WHERE e.user.id = :userId",
                        EmailMessage.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
