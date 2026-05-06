package com.mailsense.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "email_tags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmailTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    @Column(name = "tag_name", nullable = false, length = 100)
    private String tagName;
}
