package com.mailsense.service.impl;

import com.mailsense.dao.UserDao;
import com.mailsense.dao.impl.UserEmailConfigDaoImpl;
import com.mailsense.dto.RegisterDto;
import com.mailsense.entity.*;
import com.mailsense.service.AuditService;
import com.mailsense.service.UserService;
import com.mailsense.util.AesEncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired private UserDao                userDao;
    @Autowired private UserEmailConfigDaoImpl emailConfigDao;
    @Autowired private BCryptPasswordEncoder  passwordEncoder;
    @Autowired private AesEncryptionUtil      aesUtil;
    @Autowired private AuditService           auditService;

    @Override
    @Transactional
    public User register(RegisterDto dto) {
        if (userDao.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setRoleName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
            .fullName(dto.getFullName())
            .email(dto.getEmail())
            .passwordHash(passwordEncoder.encode(dto.getPassword()))
            .active(true)
            .roles(roles)
            .build();

        User saved = userDao.save(user);

        String[] enc = aesUtil.encrypt(dto.getGmailAppPassword());
        UserEmailConfig config = UserEmailConfig.builder()
            .user(saved)
            .imapHost(dto.getImapHost())
            .imapPort(dto.getImapPort())
            .smtpHost(dto.getSmtpHost())
            .smtpPort(dto.getSmtpPort())
            .emailAddress(dto.getGmailAddress())
            .appPasswordEncrypted(enc[0])
            .encryptionIv(enc[1])
            .active(true)
            .build();

        emailConfigDao.save(config);
        auditService.logById(saved.getId(), "REGISTER", "New user: " + dto.getEmail());
        log.info("Registered: {}", dto.getEmail());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userDao.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userDao.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found id=" + id));
    }

    @Override
    @Transactional
    public void resetFailedLoginAttempts(String email) {
        userDao.findByEmail(email).ifPresent(u -> {
            u.setFailedLoginAttempts(0);
            u.setLockedUntil(null);
            userDao.update(u);
        });
    }

    @Override
    @Transactional
    public int incrementFailedLoginAttempts(String email) {
        return userDao.findByEmail(email).map(u -> {
            int attempts = u.getFailedLoginAttempts() + 1;
            u.setFailedLoginAttempts(attempts);
            userDao.update(u);
            return attempts;
        }).orElse(0);
    }

    @Override
    @Transactional
    public void lockAccount(String email, int lockMinutes) {
        userDao.findByEmail(email).ifPresent(u -> {
            u.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
            userDao.update(u);
            auditService.logById(u.getId(), "ACCOUNT_LOCKED",
                "Locked " + lockMinutes + " min after failed attempts");
        });
    }
}
