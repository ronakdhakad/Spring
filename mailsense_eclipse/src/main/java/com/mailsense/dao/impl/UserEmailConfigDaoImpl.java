package com.mailsense.dao.impl;

import com.mailsense.entity.UserEmailConfig;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class UserEmailConfigDaoImpl {
    @Autowired private SessionFactory sessionFactory;

    public UserEmailConfig save(UserEmailConfig config) {
        sessionFactory.getCurrentSession().persist(config);
        return config;
    }

    public Optional<UserEmailConfig> findByUserId(Long userId) {
        UserEmailConfig c = (UserEmailConfig) sessionFactory.getCurrentSession()
            .createQuery("FROM UserEmailConfig c WHERE c.user.id = :userId AND c.active = true")
            .setParameter("userId", userId).uniqueResult();
        return Optional.ofNullable(c);
    }

    @SuppressWarnings("unchecked")
    public List<UserEmailConfig> findAllActive() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM UserEmailConfig c WHERE c.active = true").list();
    }

    public UserEmailConfig update(UserEmailConfig config) {
        return (UserEmailConfig) sessionFactory.getCurrentSession().merge(config);
    }
}
