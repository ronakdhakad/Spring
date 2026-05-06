package com.mailsense.dao.impl;

import com.mailsense.entity.WeeklyDigestLog;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WeeklyDigestLogDaoImpl {
    @Autowired private SessionFactory sessionFactory;

    public WeeklyDigestLog save(WeeklyDigestLog log) {
        sessionFactory.getCurrentSession().persist(log);
        return log;
    }
}
