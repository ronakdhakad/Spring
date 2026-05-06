package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_digest_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklyDigestLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_emails", nullable = false)
    private int totalEmails;

    @Column(name = "critical_count", nullable = false)
    private int criticalCount;

    @Column(name = "high_count", nullable = false)
    private int highCount;

    @Column(name = "unread_count", nullable = false)
    private int unreadCount;

    @Column(name = "snoozed_count", nullable = false)
    private int snoozedCount;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() { sentAt = LocalDateTime.now(); }
}
