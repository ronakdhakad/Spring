package com.emailanalyzer.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an application user.
 * Stores both login credentials and IMAP credentials for email fetching.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login email (also used as IMAP username) */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /** Application login password (plain text for demo; use BCrypt in production) */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** IMAP server hostname, e.g. imap.gmail.com */
    @Column(name = "imap_host", length = 255)
    private String imapHost = "imap.gmail.com";

    /** IMAP port, e.g. 993 for SSL */
    @Column(name = "imap_port")
    private Integer imapPort = 993;

    /** IMAP-specific password (Gmail App Password or same as login) */
    @Column(name = "imap_password", length = 255)
    private String imapPassword;

    /** Emails belonging to this user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Email> emails = new ArrayList<>();

    // ===== Constructors =====

    public User() {}

    public User(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    // ===== Getters & Setters =====

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getEmail()             { return email; }
    public void setEmail(String email)   { this.email = email; }

    public String getPassword()          { return password; }
    public void setPassword(String p)    { this.password = p; }

    public String getImapHost()          { return imapHost; }
    public void setImapHost(String h)    { this.imapHost = h; }

    public Integer getImapPort()         { return imapPort; }
    public void setImapPort(Integer p)   { this.imapPort = p; }

    public String getImapPassword()      { return imapPassword; }
    public void setImapPassword(String p){ this.imapPassword = p; }

    public List<Email> getEmails()               { return emails; }
    public void setEmails(List<Email> emails)     { this.emails = emails; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "'}";
    }
}
