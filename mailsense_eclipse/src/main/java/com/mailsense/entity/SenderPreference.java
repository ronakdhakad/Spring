package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sender_preferences",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sender_email"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SenderPreference {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "sender_email", nullable = false, length = 200)
    private String senderEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "pref_type", nullable = false, length = 20)
    private PrefType prefType;

    @Column(name = "score_modifier", nullable = false)
    private int scoreModifier;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum PrefType { WHITELIST, BLACKLIST }
}
