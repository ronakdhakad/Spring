package com.emailanalyzer.entity;

import jakarta.persistence.*;

/**
 * Urgency score for a single email.
 * Shares primary key with the Email (one-to-one via @MapsId).
 */
@Entity
@Table(name = "urgency_scores")
public class UrgencyScore {

    /** Same PK as the associated Email */
    @Id
    @Column(name = "email_id")
    private Long emailId;

    /** The email this score belongs to */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "email_id")
    private Email email;

    /** Computed or manually-set urgency score (0–100+) */
    @Column(name = "score")
    private Integer score = 0;

    /**
     * True if the user has manually changed this score.
     * When true, the scheduler will NOT re-compute it.
     */
    @Column(name = "manual_override")
    private Boolean manualOverride = false;

    /**
     * Human-readable explanation of how the score was computed.
     * E.g.:  "• 'urgent' keyword (+30)\n• Boss sender (+20)\n"
     */
    @Lob
    @Column(name = "breakdown")
    private String breakdown;

    // ===== Constructors =====

    public UrgencyScore() {}

    public UrgencyScore(Email email, int score, String breakdown) {
        this.email     = email;
        this.emailId   = email.getId();
        this.score     = score;
        this.breakdown = breakdown;
    }

    // ===== Getters & Setters =====

    public Long    getEmailId()                   { return emailId; }
    public void    setEmailId(Long emailId)        { this.emailId = emailId; }

    public Email   getEmail()                     { return email; }
    public void    setEmail(Email email)           { this.email = email; }

    public Integer getScore()                     { return score; }
    public void    setScore(Integer score)        { this.score = score; }

    public Boolean getManualOverride()            { return manualOverride; }
    public void    setManualOverride(Boolean mo)  { this.manualOverride = mo; }

    public String  getBreakdown()                 { return breakdown; }
    public void    setBreakdown(String breakdown)  { this.breakdown = breakdown; }

    @Override
    public String toString() {
        return "UrgencyScore{emailId=" + emailId + ", score=" + score + "}";
    }
}
