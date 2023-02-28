package com.cryptocurrency.investment.transaction.dto.response;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;

import java.time.LocalDateTime;

public record text(
        Double price,
        LocalDateTime localDateTime
) {
    static public text of(Double priceInfoRedis, LocalDateTime localDateTime) {
        return new text(priceInfoRedis, localDateTime);
    }
}
