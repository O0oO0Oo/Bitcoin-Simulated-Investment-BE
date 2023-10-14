package com.cryptocurrency.investment.transaction.domain;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonIgnore
    @ManyToOne
    private Crypto crypto;
    @JsonIgnore
    @ManyToOne
    private UserAccount userAccount;
    private String name;
    private double price;
    private double amount;
    @Enumerated(EnumType.STRING)
    TransactionType type;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PROGRESSED;
    private Long timestamp = System.currentTimeMillis();
}
