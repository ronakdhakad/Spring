package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emails",
    uniqueConstraints = @UniqueConstraint(name = "uq_user_message", columnNames = {"user_id", "message_id"}),
    indexes = {
        @Index(name = "idx_user_received", columnList = "user_id, received_at DESC"),
        @Index(name = "idx_user_urgency",  columnList = "user_id, urgency_level"),
        @Index(name = "idx_snooze",        columnList = "snoozed_until")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message_id", nullable = false, length = 255)
    private String messageId;

    @Column(name = "from_address", length = 255)
    private String fromAddress;

    @Column(name = "to_address", length = 500)
    private String toAddress;

    @Column(name = "subject", length = 500)
    private String subject;

    @Column(name = "body_preview", columnDefinition = "TEXT")
    private String bodyPreview;

    @Column(name = "full_body", columnDefinition = "LONGTEXT")
    private String fullBody;

    @Column(name = "urgency_score", nullable = false)
    @Builder.Default
    private int urgencyScore = 20;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false, length = 20)
    @Builder.Default
    private UrgencyLevel urgencyLevel = UrgencyLevel.LOW;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "has_attachment", nullable = false)
    @Builder.Default
    private boolean hasAttachment = false;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "snoozed_until")
    private LocalDateTime snoozedUntil;

    @Column(name = "fetched_at", nullable = false, updatable = false)
    private LocalDateTime fetchedAt;

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmailTag> tags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fetchedAt = LocalDateTime.now();
    }

    public void addTag(String tagName) {
        EmailTag tag = new EmailTag();
        tag.setEmail(this);
        tag.setTagName(tagName);
        this.tags.add(tag);
    }

    public boolean isSnoozed() {
        return snoozedUntil != null && LocalDateTime.now().isBefore(snoozedUntil);
    }

    public enum UrgencyLevel {
        CRITICAL, HIGH, MEDIUM, LOW
    }
}
