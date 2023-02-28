package com.cryptocurrency.investment.wallet.domain;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    @JsonIgnore
    @ManyToOne
    private Crypto crypto;
    @JsonIgnore
    @ManyToOne
    private UserAccount userAccount;
    private String name;
    private double totalCost = 0;
    private double amount = 0;
    @JsonIgnore
    private boolean isRevenueDisclosed = true;
}