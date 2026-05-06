package com.smartemailanalyzer.repository;

import com.smartemailanalyzer.entity.ScoreWeight;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for score weights.
 */
@Repository
public class ScoreWeightRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ScoreWeight save(ScoreWeight scoreWeight) {
        if (scoreWeight.getId() == null) {
            entityManager.persist(scoreWeight);
            return scoreWeight;
        }
        return entityManager.merge(scoreWeight);
    }

    public List<ScoreWeight> findByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT w FROM ScoreWeight w WHERE w.user.id = :userId ORDER BY w.weightKey ASC",
                        ScoreWeight.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<ScoreWeight> findByUserIdAndKey(Long userId, String weightKey) {
        try {
            ScoreWeight scoreWeight = entityManager.createQuery(
                            "SELECT w FROM ScoreWeight w WHERE w.user.id = :userId AND w.weightKey = :weightKey",
                            ScoreWeight.class)
                    .setParameter("userId", userId)
                    .setParameter("weightKey", weightKey)
                    .getSingleResult();
            return Optional.of(scoreWeight);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
