package com.mailsense.dao.impl;

import com.mailsense.entity.SentEmail;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class SentEmailDaoImpl {
    @Autowired private SessionFactory sessionFactory;

    public SentEmail save(SentEmail s) {
        sessionFactory.getCurrentSession().persist(s);
        return s;
    }

    @SuppressWarnings("unchecked")
    public List<SentEmail> findByUserId(Long userId, int page, int pageSize) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM SentEmail se WHERE se.user.id = :userId ORDER BY se.sentAt DESC")
            .setParameter("userId", userId)
            .setFirstResult(page * pageSize).setMaxResults(pageSize).list();
    }
}
