-- ============================================================
-- MailSense - Complete MySQL Schema
-- Run: mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS mailsense_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mailsense_db;

-- Run these as root to create the app user:
-- CREATE USER 'mailsense_user'@'localhost' IDENTIFIED BY 'YOUR_PASSWORD';
-- GRANT ALL PRIVILEGES ON mailsense_db.* TO 'mailsense_user'@'localhost';
-- FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS roles (
    id        BIGINT      AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS users (
    id                    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    full_name             VARCHAR(100) NOT NULL,
    email                 VARCHAR(150) NOT NULL UNIQUE,
    password_hash         VARCHAR(255) NOT NULL,
    active                TINYINT(1)   NOT NULL DEFAULT 1,
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    locked_until          DATETIME     NULL,
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_email_config (
    id                     BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id                BIGINT       NOT NULL UNIQUE,
    imap_host              VARCHAR(100) NOT NULL DEFAULT 'imap.gmail.com',
    imap_port              INT          NOT NULL DEFAULT 993,
    smtp_host              VARCHAR(100) NOT NULL DEFAULT 'smtp.gmail.com',
    smtp_port              INT          NOT NULL DEFAULT 587,
    email_address          VARCHAR(150) NOT NULL,
    app_password_encrypted VARCHAR(512) NOT NULL,
    encryption_iv          VARCHAR(100) NOT NULL,
    active                 TINYINT(1)   NOT NULL DEFAULT 1,
    last_sync_at           DATETIME     NULL,
    created_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS emails (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    message_id     VARCHAR(255) NOT NULL,
    from_address   VARCHAR(255) NULL,
    to_address     VARCHAR(500) NULL,
    subject        VARCHAR(500) NULL,
    body_preview   TEXT         NULL,
    full_body      LONGTEXT     NULL,
    urgency_score  INT          NOT NULL DEFAULT 20,
    urgency_level  ENUM('CRITICAL','HIGH','MEDIUM','LOW') NOT NULL DEFAULT 'LOW',
    is_read        TINYINT(1)   NOT NULL DEFAULT 0,
    has_attachment TINYINT(1)   NOT NULL DEFAULT 0,
    received_at    DATETIME     NULL,
    snoozed_until  DATETIME     NULL,
    fetched_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_message (user_id, message_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_received (user_id, received_at DESC),
    INDEX idx_user_urgency  (user_id, urgency_level),
    INDEX idx_snooze        (snoozed_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS email_tags (
    id       BIGINT       AUTO_INCREMENT PRIMARY KEY,
    email_id BIGINT       NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (email_id) REFERENCES emails(id) ON DELETE CASCADE,
    INDEX idx_email_tag (email_id, tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS email_rules (
    id             BIGINT      AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    field_to_match ENUM('SENDER','SUBJECT','BODY')                      NOT NULL,
    operator       ENUM('CONTAINS','EQUALS','STARTS_WITH','ENDS_WITH')  NOT NULL,
    keyword        VARCHAR(255) NOT NULL,
    tag_to_apply   VARCHAR(100) NOT NULL,
    priority       INT          NOT NULL DEFAULT 1,
    active         TINYINT(1)   NOT NULL DEFAULT 1,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_rules (user_id, active, priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sender_preferences (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    sender_email   VARCHAR(200) NOT NULL,
    pref_type      ENUM('WHITELIST','BLACKLIST') NOT NULL,
    score_modifier INT          NOT NULL DEFAULT 0,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_sender (user_id, sender_email),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sent_emails (
    id                   BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT       NOT NULL,
    in_reply_to_email_id BIGINT       NULL,
    to_address           VARCHAR(500) NOT NULL,
    cc_address           VARCHAR(500) NULL,
    subject              VARCHAR(500) NULL,
    body                 TEXT         NULL,
    send_type            ENUM('NEW','REPLY','REPLY_ALL','FORWARD') NOT NULL DEFAULT 'NEW',
    sent_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sent_user (user_id, sent_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NULL,
    action_type VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    ip_address  VARCHAR(50)  NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_user    (user_id, created_at DESC),
    INDEX idx_audit_action  (action_type),
    INDEX idx_audit_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS weekly_digest_log (
    id             BIGINT   AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT   NOT NULL,
    total_emails   INT      NOT NULL DEFAULT 0,
    critical_count INT      NOT NULL DEFAULT 0,
    high_count     INT      NOT NULL DEFAULT 0,
    unread_count   INT      NOT NULL DEFAULT 0,
    snoozed_count  INT      NOT NULL DEFAULT 0,
    sent_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Seed Data
-- ============================================================
INSERT IGNORE INTO roles (id, role_name) VALUES (1,'ROLE_USER'), (2,'ROLE_ADMIN');

-- Default admin: password = Admin@1234
INSERT IGNORE INTO users (id, full_name, email, password_hash, active)
VALUES (1,'Admin','admin@mailsense.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj2NF8b.7Vra', 1);

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 2);
