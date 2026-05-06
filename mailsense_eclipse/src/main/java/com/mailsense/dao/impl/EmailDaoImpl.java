package com.mailsense.dao.impl;

import com.mailsense.dao.EmailDao;
import com.mailsense.entity.Email;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class EmailDaoImpl implements EmailDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Email save(Email email) {
        sessionFactory.getCurrentSession().persist(email);
        return email;
    }

    @Override
    public Optional<Email> findById(Long id, Long userId) {
        Email email = (Email) sessionFactory.getCurrentSession()
            .createQuery("FROM Email e WHERE e.id = :id AND e.user.id = :userId")
            .setParameter("id", id).setParameter("userId", userId).uniqueResult();
        return Optional.ofNullable(email);
    }

    @Override
    public boolean existsByUserAndMessageId(Long userId, String messageId) {
        Long count = (Long) sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(e) FROM Email e WHERE e.user.id = :userId AND e.messageId = :messageId")
            .setParameter("userId", userId).setParameter("messageId", messageId).uniqueResult();
        return count != null && count > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Email> findByUserId(Long userId, int page, int pageSize) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Email e WHERE e.user.id = :userId AND e.snoozedUntil IS NULL ORDER BY e.urgencyScore DESC, e.receivedAt DESC")
            .setParameter("userId", userId)
            .setFirstResult(page * pageSize).setMaxResults(pageSize).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Email> findByUserIdAndTag(Long userId, String tag, int page, int pageSize) {
        return sessionFactory.getCurrentSession()
            .createQuery("SELECT DISTINCT e FROM Email e JOIN e.tags t WHERE e.user.id = :userId AND t.tagName = :tag AND e.snoozedUntil IS NULL ORDER BY e.receivedAt DESC")
            .setParameter("userId", userId).setParameter("tag", tag)
            .setFirstResult(page * pageSize).setMaxResults(pageSize).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Email> findSnoozedDue(LocalDateTime now) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Email e WHERE e.snoozedUntil IS NOT NULL AND e.snoozedUntil <= :now")
            .setParameter("now", now).list();
    }

    @Override
    public long countByUserIdAndUrgencyLevel(Long userId, Email.UrgencyLevel level) {
        Long count = (Long) sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(e) FROM Email e WHERE e.user.id = :userId AND e.urgencyLevel = :level")
            .setParameter("userId", userId).setParameter("level", level).uniqueResult();
        return count == null ? 0 : count;
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        Long count = (Long) sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(e) FROM Email e WHERE e.user.id = :userId AND e.read = false")
            .setParameter("userId", userId).uniqueResult();
        return count == null ? 0 : count;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findTopSendersByUserId(Long userId, int limit) {
        return sessionFactory.getCurrentSession()
            .createQuery("SELECT e.fromAddress, COUNT(e) as cnt FROM Email e WHERE e.user.id = :userId GROUP BY e.fromAddress ORDER BY cnt DESC")
            .setParameter("userId", userId).setMaxResults(limit).list();
    }

    @Override
    public Email update(Email email) {
        return (Email) sessionFactory.getCurrentSession().merge(email);
    }

    @Override
    public void delete(Long id, Long userId) {
        sessionFactory.getCurrentSession()
            .createQuery("DELETE FROM Email e WHERE e.id = :id AND e.user.id = :userId")
            .setParameter("id", id).setParameter("userId", userId).executeUpdate();
    }
}
