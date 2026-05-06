package com.mailsense.service;

import com.mailsense.dao.UserDao;
import com.mailsense.dao.impl.AuditLogDaoImpl;
import com.mailsense.entity.AuditLog;
import com.mailsense.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuditService {

    @Autowired private AuditLogDaoImpl auditLogDao;
    @Autowired private UserDao         userDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String userEmail, String actionType, String description, String ip) {
        try {
            Long userId = userDao.findByEmail(userEmail).map(User::getId).orElse(null);
            auditLogDao.save(AuditLog.builder()
                .userId(userId).actionType(actionType)
                .description(description).ipAddress(ip).build());
        } catch (Exception e) {
            log.error("Audit log failed for {}: {}", actionType, e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAnonymous(String actionType, String description, String ip) {
        try {
            auditLogDao.save(AuditLog.builder()
                .actionType(actionType).description(description).ipAddress(ip).build());
        } catch (Exception e) {
            log.error("Anonymous audit log failed: {}", e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logById(Long userId, String actionType, String description) {
        try {
            auditLogDao.save(AuditLog.builder()
                .userId(userId).actionType(actionType)
                .description(description).ipAddress("SYSTEM").build());
        } catch (Exception e) {
            log.error("Audit log by id failed: {}", e.getMessage());
        }
    }
}
