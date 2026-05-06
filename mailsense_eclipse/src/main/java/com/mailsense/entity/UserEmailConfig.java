package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_email_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEmailConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "imap_host", nullable = false, length = 100)
    @Builder.Default
    private String imapHost = "imap.gmail.com";

    @Column(name = "imap_port", nullable = false)
    @Builder.Default
    private int imapPort = 993;

    @Column(name = "smtp_host", nullable = false, length = 100)
    @Builder.Default
    private String smtpHost = "smtp.gmail.com";

    @Column(name = "smtp_port", nullable = false)
    @Builder.Default
    private int smtpPort = 587;

    @Column(name = "email_address", nullable = false, length = 150)
    private String emailAddress;

    @Column(name = "app_password_encrypted", nullable = false, length = 512)
    private String appPasswordEncrypted;

    @Column(name = "encryption_iv", nullable = false, length = 100)
    private String encryptionIv;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
