package com.cryptocurrency.investment.domain.redis;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@RedisHash("price")
@NoArgsConstructor
public class CurrencyPriceRedis implements Serializable{
    @Id
    private String id;

    private String currencyName;

    private Long timestamp;

    private Double price;

    public CurrencyPriceRedis(String  id, String currencyName, Long timestamp, Double price) {
        this.id = id;
        this.currencyName = currencyName;
        this.timestamp = timestamp;
        this.price = price;
    }
}



