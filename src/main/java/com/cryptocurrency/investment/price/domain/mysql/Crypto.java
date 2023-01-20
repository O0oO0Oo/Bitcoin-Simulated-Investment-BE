package com.cryptocurrency.investment.price.domain.mysql;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(indexes = {
        @Index(columnList = "name")
})
@Entity
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CryptoStatus status;
}
