package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailRule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_to_match", nullable = false, length = 20)
    private MatchField fieldToMatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 20)
    private MatchOperator operator;

    @Column(name = "keyword", nullable = false, length = 255)
    private String keyword;

    @Column(name = "tag_to_apply", nullable = false, length = 100)
    private String tagToApply;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private int priority = 1;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum MatchField   { SENDER, SUBJECT, BODY }
    public enum MatchOperator { CONTAINS, EQUALS, STARTS_WITH, ENDS_WITH }
}
