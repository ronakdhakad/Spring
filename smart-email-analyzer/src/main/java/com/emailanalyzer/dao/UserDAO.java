package com.emailanalyzer.dao;

import com.emailanalyzer.entity.User;
import java.util.List;

/**
 * Data Access Object for User entity.
 */
public interface UserDAO {

    /** Persist a new user */
    void save(User user);

    /** Update an existing user */
    void update(User user);

    /** Find user by primary key */
    User findById(Long id);

    /** Find user by email address (used for login) */
    User findByEmail(String email);

    /** Retrieve all users (for scheduler to fetch emails for everyone) */
    List<User> findAll();

    /** Delete a user */
    void delete(Long id);
}
