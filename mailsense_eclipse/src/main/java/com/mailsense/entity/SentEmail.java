package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sent_emails")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SentEmail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "in_reply_to_email_id")
    private Long inReplyToEmailId;

    @Column(name = "to_address", nullable = false, length = 500)
    private String toAddress;

    @Column(name = "cc_address", length = 500)
    private String ccAddress;

    @Column(name = "subject", length = 500)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_type", nullable = false, length = 20)
    @Builder.Default
    private SendType sendType = SendType.NEW;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() { sentAt = LocalDateTime.now(); }

    public enum SendType { NEW, REPLY, REPLY_ALL, FORWARD }
}
