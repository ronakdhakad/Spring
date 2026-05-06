package com.mailsense.scheduler;

import com.mailsense.dao.EmailDao;
import com.mailsense.dao.impl.UserEmailConfigDaoImpl;
import com.mailsense.entity.Email;
import com.mailsense.entity.UserEmailConfig;
import com.mailsense.service.AuditService;
import com.mailsense.service.DigestService;
import com.mailsense.service.EmailSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// ============================================================
// 1. Email Sync — every 5 minutes
// ============================================================
@Component
@Slf4j
class EmailSyncScheduler {

    @Autowired private EmailSyncService       syncService;
    @Autowired private UserEmailConfigDaoImpl configDao;

    @Scheduled(fixedDelayString = "${email.sync.interval.ms:300000}")
    public void syncAll() {
        log.info("EmailSyncScheduler running at {}", LocalDateTime.now());
        List<UserEmailConfig> active = configDao.findAllActive();
        for (UserEmailConfig cfg : active) {
            try {
                syncService.syncForUser(cfg.getUser());
            } catch (Exception e) {
                log.error("Sync failed user {}: {}", cfg.getUser().getId(), e.getMessage());
            }
        }
    }
}

// ============================================================
// 2. Snooze Wake — every 1 minute
// ============================================================
@Component
@Slf4j
class SnoozeScheduler {

    @Autowired private EmailDao     emailDao;
    @Autowired private AuditService auditService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void wakeExpired() {
        List<Email> due = emailDao.findSnoozedDue(LocalDateTime.now());
        if (due.isEmpty()) return;
        log.info("Waking {} snoozed emails", due.size());
        for (Email e : due) {
            e.setSnoozedUntil(null);
            e.setRead(false);
            emailDao.update(e);
            auditService.logById(e.getUser().getId(), "SNOOZE_WOKE",
                "Email woke: " + e.getSubject());
        }
    }
}

// ============================================================
// 3. Weekly Digest — every Sunday midnight
// ============================================================
@Component
@Slf4j
class DigestScheduler {

    @Autowired private DigestService          digestService;
    @Autowired private UserEmailConfigDaoImpl configDao;

    @Scheduled(cron = "0 0 0 * * SUN")
    public void sendDigests() {
        log.info("DigestScheduler running at {}", LocalDateTime.now());
        List<UserEmailConfig> active = configDao.findAllActive();
        for (UserEmailConfig cfg : active) {
            try {
                digestService.sendDigest(cfg.getUser());
            } catch (Exception e) {
                log.error("Digest failed user {}: {}", cfg.getUser().getId(), e.getMessage());
            }
        }
    }
}
