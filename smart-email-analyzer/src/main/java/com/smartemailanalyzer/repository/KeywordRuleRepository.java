package com.smartemailanalyzer.repository;

import com.smartemailanalyzer.entity.KeywordRule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for keyword rules.
 */
@Repository
public class KeywordRuleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public KeywordRule save(KeywordRule rule) {
        if (rule.getId() == null) {
            entityManager.persist(rule);
            return rule;
        }
        return entityManager.merge(rule);
    }

    public List<KeywordRule> findByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT r FROM KeywordRule r WHERE r.user.id = :userId ORDER BY r.keyword ASC",
                        KeywordRule.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<KeywordRule> findByIdAndUserId(Long ruleId, Long userId) {
        List<KeywordRule> result = entityManager.createQuery(
                        "SELECT r FROM KeywordRule r WHERE r.id = :ruleId AND r.user.id = :userId",
                        KeywordRule.class)
                .setParameter("ruleId", ruleId)
                .setParameter("userId", userId)
                .getResultList();
        return result.stream().findFirst();
    }

    public void delete(KeywordRule rule) {
        entityManager.remove(entityManager.contains(rule) ? rule : entityManager.merge(rule));
    }
}
