package com.emailanalyzer.entity;

import jakarta.persistence.*;

/**
 * A scoring rule.
 * Type = KEYWORD: if email body/subject contains 'value', add 'score' and assign 'tag'.
 * Type = SENDER:  if email sender matches 'value', add 'score' and assign 'tag'.
 */
@Entity
@Table(name = "rules")
public class Rule {

    public static final String TYPE_KEYWORD = "KEYWORD";
    public static final String TYPE_SENDER  = "SENDER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Rule type: KEYWORD or SENDER */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** The keyword text or sender email address to match */
    @Column(name = "value", nullable = false, length = 500)
    private String value;

    /** Tag to assign when this rule matches (may be null for score-only rules) */
    @Column(name = "tag", length = 100)
    private String tag;

    /** Score contribution (can be negative for spam-like signals) */
    @Column(name = "score")
    private Integer score = 0;

    // ===== Constructors =====

    public Rule() {}

    public Rule(String type, String value, String tag, Integer score) {
        this.type  = type;
        this.value = value;
        this.tag   = tag;
        this.score = score;
    }

    // ===== Getters & Setters =====

    public Long    getId()              { return id; }
    public void    setId(Long id)       { this.id = id; }

    public String  getType()            { return type; }
    public void    setType(String type) { this.type = type; }

    public String  getValue()             { return value; }
    public void    setValue(String value) { this.value = value; }

    public String  getTag()             { return tag; }
    public void    setTag(String tag)   { this.tag = tag; }

    public Integer getScore()               { return score; }
    public void    setScore(Integer score)  { this.score = score; }

    @Override
    public String toString() {
        return "Rule{type='" + type + "', value='" + value
             + "', tag='" + tag + "', score=" + score + "}";
    }
}
