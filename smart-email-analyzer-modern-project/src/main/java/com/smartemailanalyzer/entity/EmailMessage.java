package com.smartemailanalyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parsed email saved for a specific user.
 */
@Entity
@Table(name = "emails", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_message_uid", columnNames = {"user_id", "message_uid"})
})
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message_uid", nullable = false, length = 100)
    private String messageUid;

    @Column(nullable = false, length = 255)
    private String sender;

    @Column(name = "sender_email", length = 160)
    private String senderEmail;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(name = "received_time", nullable = false)
    private LocalDateTime receivedTime;

    @Lob
    @Column(name = "raw_content", columnDefinition = "LONGTEXT")
    private String rawContent;

    @Lob
    @Column(name = "cleaned_content", columnDefinition = "LONGTEXT")
    private String cleanedContent;

    @Column(name = "tags_csv", length = 500)
    private String tagsCsv;

    @Column(name = "urgency_score", nullable = false)
    private int urgencyScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public String getReceivedTimeDisplay() {
        if (receivedTime == null) {
            return "-";
        }
        return receivedTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    public List<String> getTagList() {
        if (tagsCsv == null || tagsCsv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tagsCsv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getCleanedContent() {
        return cleanedContent;
    }

    public void setCleanedContent(String cleanedContent) {
        this.cleanedContent = cleanedContent;
    }

    public String getTagsCsv() {
        return tagsCsv;
    }

    public void setTagsCsv(String tagsCsv) {
        this.tagsCsv = tagsCsv;
    }

    public int getUrgencyScore() {
        return urgencyScore;
    }

    public void setUrgencyScore(int urgencyScore) {
        this.urgencyScore = urgencyScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
