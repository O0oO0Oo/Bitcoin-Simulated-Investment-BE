package com.cryptocurrency.investment.price.domain.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@RedisHash("price")
@AllArgsConstructor
public class PriceInfoRedis implements Serializable{
    
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed
    private Long timestamp;

    private String price;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private int expiration;
}

