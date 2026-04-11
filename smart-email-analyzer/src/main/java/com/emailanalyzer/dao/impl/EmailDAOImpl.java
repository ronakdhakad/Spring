package com.emailanalyzer.dao.impl;

import com.emailanalyzer.dao.EmailDAO;
import com.emailanalyzer.entity.Email;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Hibernate-based implementation of EmailDAO.
 */
@Repository
@Transactional
public class EmailDAOImpl implements EmailDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Email email) {
        sessionFactory.getCurrentSession().persist(email);
    }

    @Override
    public void update(Email email) {
        sessionFactory.getCurrentSession().merge(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Email findById(Long id) {
        return sessionFactory.getCurrentSession().get(Email.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMessageId(String messageId) {
        if (messageId == null) return false;
        Long count = sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(e) FROM Email e WHERE e.messageId = :mid", Long.class)
            .setParameter("mid", messageId)
            .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> findByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
            .createQuery(
                "FROM Email e WHERE e.user.id = :uid ORDER BY e.receivedTime DESC",
                Email.class)
            .setParameter("uid", userId)
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> findByUserIdAndTag(Long userId, String tagName) {
        return sessionFactory.getCurrentSession()
            .createQuery(
                "SELECT DISTINCT e FROM Email e " +
                "JOIN e.tags t " +
                "WHERE e.user.id = :uid AND UPPER(t.name) = UPPER(:tagName) " +
                "ORDER BY e.receivedTime DESC",
                Email.class)
            .setParameter("uid", userId)
            .setParameter("tagName", tagName)
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> searchBySubject(Long userId, String keyword) {
        return sessionFactory.getCurrentSession()
            .createQuery(
                "FROM Email e WHERE e.user.id = :uid " +
                "AND LOWER(e.subject) LIKE LOWER(:kw) " +
                "ORDER BY e.receivedTime DESC",
                Email.class)
            .setParameter("uid", userId)
            .setParameter("kw", "%" + keyword + "%")
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> findTodayByUserId(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = startOfDay.plusDays(1);
        return sessionFactory.getCurrentSession()
            .createQuery(
                "FROM Email e WHERE e.user.id = :uid " +
                "AND e.receivedTime >= :start AND e.receivedTime < :end",
                Email.class)
            .setParameter("uid",   userId)
            .setParameter("start", startOfDay)
            .setParameter("end",   endOfDay)
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> findByUserIdSortedByScore(Long userId) {
        return sessionFactory.getCurrentSession()
            .createQuery(
                "SELECT e FROM Email e " +
                "LEFT JOIN e.urgencyScore us " +
                "WHERE e.user.id = :uid " +
                "ORDER BY COALESCE(us.score, 0) DESC",
                Email.class)
            .setParameter("uid", userId)
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> findAll() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Email", Email.class)
            .list();
    }

    @Override
    public void delete(Long id) {
        Email email = findById(id);
        if (email != null) {
            sessionFactory.getCurrentSession().remove(email);
        }
    }
}
