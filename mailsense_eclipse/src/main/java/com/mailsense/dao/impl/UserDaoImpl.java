package com.mailsense.dao.impl;

import com.mailsense.dao.UserDao;
import com.mailsense.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User save(User user) {
        sessionFactory.getCurrentSession().persist(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(
            sessionFactory.getCurrentSession().get(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = (User) sessionFactory.getCurrentSession()
            .createQuery("FROM User u WHERE u.email = :email AND u.active = true")
            .setParameter("email", email)
            .uniqueResult();
        return Optional.ofNullable(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = (Long) sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email")
            .setParameter("email", email).uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public User update(User user) {
        return (User) sessionFactory.getCurrentSession().merge(user);
    }
}
