package com.cryptocurrency.investment.price.dto;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;

import java.util.List;

public record text(
        List<PriceInfoRedis> priceInfoRedis
) {
    static public text of(List<PriceInfoRedis> priceInfoRedis) {
        return new text(priceInfoRedis);
    }
}
