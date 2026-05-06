package com.mailsense.dao.impl;

import com.mailsense.entity.AuditLog;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AuditLogDaoImpl {
    @Autowired private SessionFactory sessionFactory;

    public AuditLog save(AuditLog log) {
        sessionFactory.getCurrentSession().persist(log);
        return log;
    }

    @SuppressWarnings("unchecked")
    public List<AuditLog> findByUserId(Long userId, int page, int pageSize) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM AuditLog al WHERE al.userId = :userId ORDER BY al.createdAt DESC")
            .setParameter("userId", userId)
            .setFirstResult(page * pageSize).setMaxResults(pageSize).list();
    }
}
