package com.mailsense.service;

import com.mailsense.dao.EmailDao;
import com.mailsense.dao.impl.UserEmailConfigDaoImpl;
import com.mailsense.entity.Email;
import com.mailsense.entity.User;
import com.mailsense.entity.UserEmailConfig;
import com.mailsense.util.AesEncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmailSyncService {

    @Autowired private ImapFetcherService     imapFetcher;
    @Autowired private TagRuleEngine          tagEngine;
    @Autowired private UrgencyScoreEngine     scoreEngine;
    @Autowired private EmailDao               emailDao;
    @Autowired private UserEmailConfigDaoImpl configDao;
    @Autowired private AesEncryptionUtil      aesUtil;
    @Autowired private AuditService           auditService;

    @Transactional
    public int syncForUser(User user) {
        Long userId = user.getId();

        UserEmailConfig config = configDao.findByUserId(userId).orElse(null);
        if (config == null || !config.isActive()) return 0;

        String plainPass;
        try {
            plainPass = aesUtil.decrypt(config.getAppPasswordEncrypted(), config.getEncryptionIv());
        } catch (Exception e) {
            log.error("Decrypt failed for user {}: {}", userId, e.getMessage());
            auditService.logById(userId, "EMAIL_SYNC_ERROR", "Decryption failed");
            return 0;
        }

        List<Email> fetched;
        try {
            fetched = imapFetcher.fetchNewEmails(config, plainPass);
        } catch (Exception e) {
            log.error("IMAP fetch failed for user {}: {}", userId, e.getMessage());
            auditService.logById(userId, "EMAIL_SYNC_ERROR", "IMAP: " + e.getMessage());
            return 0;
        }

        int saved = 0;
        for (Email email : fetched) {
            if (emailDao.existsByUserAndMessageId(userId, email.getMessageId())) continue;
            email.setUser(user);
            tagEngine.applyRules(email, userId);
            scoreEngine.calculateAndApply(email, userId);
            emailDao.save(email);
            saved++;
        }

        config.setLastSyncAt(LocalDateTime.now());
        configDao.update(config);

        if (saved > 0)
            auditService.logById(userId, "EMAIL_FETCH", "Fetched " + saved + " new emails");

        log.info("Sync done for user {}: {} new emails", userId, saved);
        return saved;
    }
}
