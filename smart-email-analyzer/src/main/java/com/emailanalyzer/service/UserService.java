package com.emailanalyzer.service;

import com.emailanalyzer.entity.User;

public interface UserService {

    /** Validate credentials and return the User, or null if invalid */
    User login(String email, String password);

    User findById(Long id);

    void save(User user);

    void update(User user);
}
