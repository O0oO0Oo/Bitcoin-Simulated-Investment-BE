package com.cryptocurrency.investment.domain.redis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

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
}



