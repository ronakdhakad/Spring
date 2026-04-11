package com.emailanalyzer.dao.impl;

import com.emailanalyzer.dao.TagDAO;
import com.emailanalyzer.entity.Tag;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TagDAOImpl implements TagDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Tag tag) {
        sessionFactory.getCurrentSession().persist(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return sessionFactory.getCurrentSession().get(Tag.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Tag t WHERE UPPER(t.name) = UPPER(:name)", Tag.class)
            .setParameter("name", name)
            .uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Tag ORDER BY name", Tag.class)
            .list();
    }

    @Override
    public void delete(Long id) {
        Tag tag = findById(id);
        if (tag != null) {
            sessionFactory.getCurrentSession().remove(tag);
        }
    }
}
