package com.cryptocurrency.investment.domain.redis;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@RedisHash
@NoArgsConstructor
public class CurrencyPriceRedis implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currencyName;

    private LocalDateTime timestamp;

    private Double price;

    public CurrencyPriceRedis(Long id, String currencyName, LocalDateTime timestamp, Double price) {
        this.id = id;
        this.currencyName = currencyName;
        this.timestamp = timestamp;
        this.price = price;
    }
}



