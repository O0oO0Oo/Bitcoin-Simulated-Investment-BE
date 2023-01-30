package com.cryptocurrency.investment.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

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

    @TimeToLive(unit = TimeUnit.SECONDS)
    private int expiration;

    public EmailValidation(String email, String validation) {
        this.email = email;
        this.validation = validation;
        this.expiration = 60;
    }
}