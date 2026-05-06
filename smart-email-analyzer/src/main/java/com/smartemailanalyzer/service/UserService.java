package com.smartemailanalyzer.service;

import com.smartemailanalyzer.dto.RegistrationForm;
import com.smartemailanalyzer.entity.User;
import com.smartemailanalyzer.repository.UserRepository;
import com.smartemailanalyzer.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * User-related business logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RuleService ruleService;

    public UserService(UserRepository userRepository, RuleService ruleService) {
        this.userRepository = userRepository;
        this.ruleService = ruleService;
    }

    @Transactional
    public User register(RegistrationForm form) {
        String normalizedEmail = normalizeEmail(form.getEmail());
        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new IllegalArgumentException("Email is already registered.");
        });

        User user = new User();
        user.setName(form.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(PasswordUtil.hash(form.getPassword()));
        user.setGoogleAppPassword(form.getGoogleAppPassword().trim());
        user.setLoggedIn(false);
        user.setImapLastUid(null);

        User savedUser = userRepository.save(user);
        ruleService.initializeDefaultsForUser(savedUser);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        return userRepository.findByEmail(normalizedEmail)
                .filter(user -> PasswordUtil.matches(rawPassword, user.getPasswordHash()));
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email));
    }

    @Transactional(readOnly = true)
    public List<User> getLoggedInUsers() {
        return userRepository.findLoggedInUsers();
    }

    @Transactional
    public void updateLoggedInStatus(Long userId, boolean loggedIn) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLoggedIn(loggedIn);
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateImapLastUid(Long userId, Long imapLastUid) {
        if (imapLastUid == null) {
            return;
        }
        userRepository.findById(userId).ifPresent(user -> {
            user.setImapLastUid(imapLastUid);
            userRepository.save(user);
        });
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
