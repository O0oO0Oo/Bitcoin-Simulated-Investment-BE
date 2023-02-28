package com.cryptocurrency.investment.crypto.domain;

import com.cryptocurrency.investment.user.domain.UserAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FavoriteCrypto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JsonIgnore
    private Crypto crypto;
    @ManyToOne
    @JsonIgnore
    private UserAccount userAccount;
}
