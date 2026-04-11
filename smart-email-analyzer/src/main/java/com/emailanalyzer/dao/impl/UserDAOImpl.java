package com.emailanalyzer.dao.impl;

import com.emailanalyzer.dao.UserDAO;
import com.emailanalyzer.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate-based implementation of UserDAO.
 */
@Repository
@Transactional
public class UserDAOImpl implements UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    @Override
    public void update(User user) {
        sessionFactory.getCurrentSession().merge(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return sessionFactory.getCurrentSession().get(User.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM User u WHERE u.email = :email", User.class)
            .setParameter("email", email)
            .uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM User", User.class)
            .list();
    }

    @Override
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            sessionFactory.getCurrentSession().remove(user);
        }
    }
}
