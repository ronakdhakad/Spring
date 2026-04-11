package com.emailanalyzer.dao.impl;

import com.emailanalyzer.dao.RuleDAO;
import com.emailanalyzer.entity.Rule;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class RuleDAOImpl implements RuleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Rule rule) {
        sessionFactory.getCurrentSession().persist(rule);
    }

    @Override
    public void update(Rule rule) {
        sessionFactory.getCurrentSession().merge(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public Rule findById(Long id) {
        return sessionFactory.getCurrentSession().get(Rule.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rule> findAll() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Rule ORDER BY type, value", Rule.class)
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rule> findByType(String type) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Rule r WHERE r.type = :type", Rule.class)
            .setParameter("type", type)
            .list();
    }

    @Override
    public void delete(Long id) {
        Rule rule = findById(id);
        if (rule != null) {
            sessionFactory.getCurrentSession().remove(rule);
        }
    }
}
