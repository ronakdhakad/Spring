package com.emailanalyzer.entity;

import jakarta.persistence.*;

/**
 * Email classification tag (e.g. URGENT, PAYMENT, MEETING, PROMOTION).
 */
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    // ===== Constructors =====

    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    // ===== Getters & Setters =====

    public Long getId()           { return id; }
    public void setId(Long id)    { this.id = id; }

    public String getName()       { return name; }
    public void setName(String n) { this.name = n; }

    @Override
    public String toString()      { return "Tag{" + name + "}"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return name != null && name.equalsIgnoreCase(tag.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.toLowerCase().hashCode() : 0;
    }
}
