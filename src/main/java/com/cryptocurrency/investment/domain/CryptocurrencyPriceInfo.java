package com.cryptocurrency.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Table(indexes = {
        @Index(columnList = "currencyName"),
        @Index(columnList = "timestamp")
})
@Entity
public class CryptocurrencyPriceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String currencyName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Long price;

}
