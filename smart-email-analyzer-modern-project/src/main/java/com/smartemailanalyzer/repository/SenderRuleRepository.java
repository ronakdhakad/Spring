package com.smartemailanalyzer.repository;

import com.smartemailanalyzer.entity.SenderRule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for sender rules.
 */
@Repository
public class SenderRuleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public SenderRule save(SenderRule rule) {
        if (rule.getId() == null) {
            entityManager.persist(rule);
            return rule;
        }
        return entityManager.merge(rule);
    }

    public List<SenderRule> findByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT r FROM SenderRule r WHERE r.user.id = :userId ORDER BY r.senderEmail ASC",
                        SenderRule.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<SenderRule> findByUserIdAndSenderEmail(Long userId, String senderEmail) {
        try {
            SenderRule senderRule = entityManager.createQuery(
                            "SELECT r FROM SenderRule r WHERE r.user.id = :userId AND LOWER(r.senderEmail) = :senderEmail",
                            SenderRule.class)
                    .setParameter("userId", userId)
                    .setParameter("senderEmail", senderEmail.toLowerCase())
                    .getSingleResult();
            return Optional.of(senderRule);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<SenderRule> findByIdAndUserId(Long ruleId, Long userId) {
        List<SenderRule> result = entityManager.createQuery(
                        "SELECT r FROM SenderRule r WHERE r.id = :ruleId AND r.user.id = :userId",
                        SenderRule.class)
                .setParameter("ruleId", ruleId)
                .setParameter("userId", userId)
                .getResultList();
        return result.stream().findFirst();
    }

    public void delete(SenderRule rule) {
        entityManager.remove(entityManager.contains(rule) ? rule : entityManager.merge(rule));
    }
}
