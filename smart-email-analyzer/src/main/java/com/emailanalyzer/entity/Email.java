package com.emailanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single fetched email stored in the database.
 */
@Entity
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user this email belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Unique IMAP Message-ID header value.
     * Used to prevent storing duplicate emails.
     */
    @Column(name = "message_id", unique = true, length = 512)
    private String messageId;

    /** Sender email address */
    @Column(name = "sender", length = 255)
    private String sender;

    /** Email subject */
    @Column(name = "subject", length = 1000)
    private String subject;

    /** Plain-text body of the email */
    @Lob
    @Column(name = "body")
    private String body;

    /** When the email was received */
    @Column(name = "received_time")
    private LocalDateTime receivedTime;

    /** Tags assigned by the Rule Engine (URGENT, PAYMENT, etc.) */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "email_tags",
        joinColumns        = @JoinColumn(name = "email_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /** Urgency score assigned by the Rule Engine */
    @OneToOne(mappedBy = "email", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UrgencyScore urgencyScore;

    // ===== Constructors =====

    public Email() {}

    // ===== Getters & Setters =====

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public User getUser()                        { return user; }
    public void setUser(User user)               { this.user = user; }

    public String getMessageId()                 { return messageId; }
    public void setMessageId(String messageId)   { this.messageId = messageId; }

    public String getSender()                    { return sender; }
    public void setSender(String sender)         { this.sender = sender; }

    public String getSubject()                   { return subject; }
    public void setSubject(String subject)       { this.subject = subject; }

    public String getBody()                      { return body; }
    public void setBody(String body)             { this.body = body; }

    public LocalDateTime getReceivedTime()              { return receivedTime; }
    public void setReceivedTime(LocalDateTime t)        { this.receivedTime = t; }

    public Set<Tag> getTags()                    { return tags; }
    public void setTags(Set<Tag> tags)           { this.tags = tags; }

    public UrgencyScore getUrgencyScore()               { return urgencyScore; }
    public void setUrgencyScore(UrgencyScore us)        { this.urgencyScore = us; }

    /** Convenience: get urgency score value, default 0 */
    public int getScoreValue() {
        return (urgencyScore != null) ? urgencyScore.getScore() : 0;
    }

    @Override
    public String toString() {
        return "Email{id=" + id + ", sender='" + sender + "', subject='" + subject + "'}";
    }
}
