package com.cryptocurrency.investment.crypto.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public Crypto(String name, CryptoStatus status) {
        this.name = name;
        this.status = status;
    }
}
