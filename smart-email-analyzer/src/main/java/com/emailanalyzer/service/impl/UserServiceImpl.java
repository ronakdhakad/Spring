package com.emailanalyzer.service.impl;

import com.emailanalyzer.dao.UserDAO;
import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        if (email == null || password == null) return null;
        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            log.warn("Login attempt for unknown email: {}", email);
            return null;
        }
        // Plain-text comparison (replace with BCrypt in production)
        if (!password.equals(user.getPassword())) {
            log.warn("Wrong password for: {}", email);
            return null;
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userDAO.findById(id);
    }

    @Override
    public void save(User user) {
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        userDAO.save(user);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }
}
