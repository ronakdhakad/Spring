package com.smartemailanalyzer.repository;

import com.smartemailanalyzer.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for users.
 */
@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        }
        return entityManager.merge(user);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public Optional<User> findByEmail(String email) {
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE LOWER(u.email) = :email", User.class)
                    .setParameter("email", email.toLowerCase())
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<User> findLoggedInUsers() {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.loggedIn = true ORDER BY u.id ASC", User.class)
                .getResultList();
    }
}
