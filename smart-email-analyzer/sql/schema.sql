-- ============================================================
-- Smart Email Analyzer - MySQL 8 Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS email_analyzer
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE email_analyzer;

-- ============================================================
-- Table: users
-- Stores application login + IMAP credentials
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    imap_host     VARCHAR(255) DEFAULT 'imap.gmail.com',
    imap_port     INT          DEFAULT 993,
    imap_password VARCHAR(255) COMMENT 'App password or mail password for IMAP'
) ENGINE=InnoDB;

-- ============================================================
-- Table: emails
-- Stores fetched emails per user
-- ============================================================
CREATE TABLE IF NOT EXISTS emails (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    message_id    VARCHAR(512) UNIQUE COMMENT 'IMAP Message-ID to prevent duplicates',
    sender        VARCHAR(255),
    subject       VARCHAR(1000),
    body          LONGTEXT,
    received_time DATETIME,
    CONSTRAINT fk_emails_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: tags
-- E.g. URGENT, PAYMENT, MEETING, PROMOTION
-- ============================================================
CREATE TABLE IF NOT EXISTS tags (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ============================================================
-- Table: email_tags (Many-to-Many)
-- ============================================================
CREATE TABLE IF NOT EXISTS email_tags (
    email_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (email_id, tag_id),
    CONSTRAINT fk_et_email FOREIGN KEY (email_id) REFERENCES emails (id) ON DELETE CASCADE,
    CONSTRAINT fk_et_tag   FOREIGN KEY (tag_id)   REFERENCES tags   (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: rules
-- Keyword→Tag or Sender importance rules
-- ============================================================
CREATE TABLE IF NOT EXISTS rules (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    type  VARCHAR(50)  NOT NULL COMMENT 'KEYWORD or SENDER',
    value VARCHAR(500) NOT NULL COMMENT 'The keyword or sender email',
    tag   VARCHAR(100)          COMMENT 'Tag to assign when rule matches',
    score INT          DEFAULT 0 COMMENT 'Urgency score contribution'
) ENGINE=InnoDB;

-- ============================================================
-- Table: urgency_scores
-- One score row per email
-- ============================================================
CREATE TABLE IF NOT EXISTS urgency_scores (
    email_id        BIGINT  PRIMARY KEY,
    score           INT     DEFAULT 0,
    manual_override BOOLEAN DEFAULT FALSE,
    breakdown       TEXT    COMMENT 'JSON or text explanation of score',
    CONSTRAINT fk_us_email FOREIGN KEY (email_id) REFERENCES emails (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX idx_emails_user_id       ON emails (user_id);
CREATE INDEX idx_emails_received_time ON emails (received_time);
CREATE INDEX idx_rules_type           ON rules  (type);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Default tags
INSERT INTO tags (name) VALUES
    ('URGENT'),
    ('PAYMENT'),
    ('PROMOTION'),
    ('MEETING'),
    ('WORK');

-- Default keyword rules
INSERT INTO rules (type, value, tag, score) VALUES
    ('KEYWORD', 'urgent',          'URGENT',    30),
    ('KEYWORD', 'asap',            'URGENT',    25),
    ('KEYWORD', 'immediately',     'URGENT',    20),
    ('KEYWORD', 'deadline',        'URGENT',    20),
    ('KEYWORD', 'important',       'URGENT',    15),
    ('KEYWORD', 'action required', 'URGENT',    25),
    ('KEYWORD', 'payment',         'PAYMENT',   25),
    ('KEYWORD', 'invoice',         'PAYMENT',   20),
    ('KEYWORD', 'due',             'PAYMENT',   10),
    ('KEYWORD', 'overdue',         'PAYMENT',   20),
    ('KEYWORD', 'meeting',         'MEETING',   15),
    ('KEYWORD', 'interview',       'MEETING',   15),
    ('KEYWORD', 'schedule',        'MEETING',   10),
    ('KEYWORD', 'offer',           'PROMOTION',  8),
    ('KEYWORD', 'discount',        'PROMOTION',  5),
    ('KEYWORD', 'sale',            'PROMOTION',  5),
    ('KEYWORD', 'unsubscribe',     'PROMOTION', -5);

-- Sample sender-importance rules
INSERT INTO rules (type, value, tag, score) VALUES
    ('SENDER', 'boss@company.com',   'WORK', 20),
    ('SENDER', 'hr@company.com',     'WORK', 15),
    ('SENDER', 'noreply@bank.com',   'PAYMENT', 20),
    ('SENDER', 'support@company.com','WORK', 10);

-- Demo user
-- NOTE: In production use BCrypt hashing; here plain text for demo
INSERT INTO users (email, password, imap_host, imap_port, imap_password)
VALUES
    ('dhakadronak989@gmail.com', '123', 'imap.gmail.com', 993, 'igaeamriztaqrbsy'),
    ('dhakadr932@gmail.com',  '123', 'imap.gmail.com', 993, 'niuhktwsmtrlyopl');
