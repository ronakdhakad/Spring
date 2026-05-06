CREATE DATABASE IF NOT EXISTS smart_email_analyzer
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_email_analyzer;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    google_app_password VARCHAR(255) NOT NULL,
    logged_in BOOLEAN NOT NULL DEFAULT FALSE,
    imap_last_uid BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
);

CREATE TABLE IF NOT EXISTS emails (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    message_uid VARCHAR(100) NOT NULL,
    sender VARCHAR(255) NOT NULL,
    sender_email VARCHAR(160) NULL,
    subject VARCHAR(500) NOT NULL,
    received_time DATETIME NOT NULL,
    raw_content LONGTEXT NULL,
    cleaned_content LONGTEXT NULL,
    tags_csv VARCHAR(500) NULL,
    urgency_score INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_emails_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_message_uid (user_id, message_uid),
    KEY idx_emails_user_received (user_id, received_time),
    KEY idx_emails_user_score (user_id, urgency_score)
);

CREATE TABLE IF NOT EXISTS keyword_rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    keyword VARCHAR(120) NOT NULL,
    tag VARCHAR(60) NOT NULL,
    score_boost INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_keyword_rules_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    KEY idx_keyword_rules_user_keyword (user_id, keyword)
);

CREATE TABLE IF NOT EXISTS sender_rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    sender_email VARCHAR(160) NOT NULL,
    score_boost INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_sender_rules_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_sender_rule (user_id, sender_email)
);

CREATE TABLE IF NOT EXISTS score_weights (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    weight_key VARCHAR(100) NOT NULL,
    weight_value INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_score_weights_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_weight_key (user_id, weight_key)
);
