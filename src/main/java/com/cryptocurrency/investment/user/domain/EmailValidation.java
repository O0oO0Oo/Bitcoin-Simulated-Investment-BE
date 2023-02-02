package com.cryptocurrency.investment.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@NoArgsConstructor
public class EmailValidation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;

    private String validation;

    private LocalDateTime timestamp;

    public EmailValidation(String email, String validation) {
        this.email = email;
        this.validation = validation;
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}