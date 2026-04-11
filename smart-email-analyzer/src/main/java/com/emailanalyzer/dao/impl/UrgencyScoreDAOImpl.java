package com.emailanalyzer.dao.impl;

import com.emailanalyzer.dao.UrgencyScoreDAO;
import com.emailanalyzer.entity.UrgencyScore;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UrgencyScoreDAOImpl implements UrgencyScoreDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(UrgencyScore urgencyScore) {
        sessionFactory.getCurrentSession().persist(urgencyScore);
    }

    @Override
    public void update(UrgencyScore urgencyScore) {
        sessionFactory.getCurrentSession().merge(urgencyScore);
    }

    @Override
    @Transactional(readOnly = true)
    public UrgencyScore findByEmailId(Long emailId) {
        return sessionFactory.getCurrentSession().get(UrgencyScore.class, emailId);
    }

    @Override
    public void delete(Long emailId) {
        UrgencyScore us = findByEmailId(emailId);
        if (us != null) {
            sessionFactory.getCurrentSession().remove(us);
        }
    }
}
