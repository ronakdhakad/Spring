package com.mailsense.dao.impl;

import com.mailsense.entity.SenderPreference;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class SenderPrefDaoImpl {

    @Autowired
    private SessionFactory sessionFactory;

    public SenderPreference save(SenderPreference pref) {
        sessionFactory.getCurrentSession().persist(pref);
        return pref;
    }

    @SuppressWarnings("unchecked")
    public List<SenderPreference> findByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM SenderPreference sp WHERE sp.user.id = :userId ORDER BY sp.prefType ASC")
            .setParameter("userId", userId).list();
    }

    public Optional<SenderPreference> findByUserIdAndEmail(Long userId, String senderEmail) {
        SenderPreference sp = (SenderPreference) sessionFactory.getCurrentSession()
            .createQuery("FROM SenderPreference sp WHERE sp.user.id = :userId AND sp.senderEmail = :senderEmail")
            .setParameter("userId", userId).setParameter("senderEmail", senderEmail).uniqueResult();
        return Optional.ofNullable(sp);
    }

    public void delete(Long id, Long userId) {
        sessionFactory.getCurrentSession()
            .createQuery("DELETE FROM SenderPreference sp WHERE sp.id = :id AND sp.user.id = :userId")
            .setParameter("id", id).setParameter("userId", userId).executeUpdate();
    }
}
