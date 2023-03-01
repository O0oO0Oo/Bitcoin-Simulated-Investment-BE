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

    private Integer code;

    private LocalDateTime timestamp;

    public EmailValidation(String email, Integer code) {
        this.email = email;
        this.code = code;
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}