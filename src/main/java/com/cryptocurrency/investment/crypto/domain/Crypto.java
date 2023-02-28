package com.cryptocurrency.investment.crypto.domain;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(indexes = {
        @Index(columnList = "name")
})
@Entity
@NoArgsConstructor
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 20, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CryptoStatus status;
    @OneToMany(mappedBy = "crypto")
    private List<Wallet> wallets = new ArrayList<>();
    @OneToMany(mappedBy = "crypto")
    private List<Transaction> transactions = new ArrayList<>();
    @OneToMany(mappedBy = "crypto")
    private List<FavoriteCrypto> favoriteCryptos = new ArrayList<>();
    public Crypto(String name, CryptoStatus status) {
        this.name = name;
        this.status = status;
    }
}
