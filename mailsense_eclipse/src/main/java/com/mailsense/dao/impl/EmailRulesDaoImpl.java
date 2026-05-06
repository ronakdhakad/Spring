package com.mailsense.dao.impl;

import com.mailsense.entity.EmailRule;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmailRulesDaoImpl {

    @Autowired
    private SessionFactory sessionFactory;

    public EmailRule save(EmailRule rule) {
        sessionFactory.getCurrentSession().persist(rule);
        return rule;
    }

    @SuppressWarnings("unchecked")
    public List<EmailRule> findActiveByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM EmailRule r WHERE r.user.id = :userId AND r.active = true ORDER BY r.priority ASC")
            .setParameter("userId", userId).list();
    }

    public Optional<EmailRule> findByIdAndUserId(Long id, Long userId) {
        EmailRule rule = (EmailRule) sessionFactory.getCurrentSession()
            .createQuery("FROM EmailRule r WHERE r.id = :id AND r.user.id = :userId")
            .setParameter("id", id).setParameter("userId", userId).uniqueResult();
        return Optional.ofNullable(rule);
    }

    public EmailRule update(EmailRule rule) {
        return (EmailRule) sessionFactory.getCurrentSession().merge(rule);
    }

    public void delete(Long id, Long userId) {
        sessionFactory.getCurrentSession()
            .createQuery("DELETE FROM EmailRule r WHERE r.id = :id AND r.user.id = :userId")
            .setParameter("id", id).setParameter("userId", userId).executeUpdate();
    }
}
