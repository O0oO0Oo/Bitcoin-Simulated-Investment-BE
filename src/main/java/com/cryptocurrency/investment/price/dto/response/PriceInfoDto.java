package com.cryptocurrency.investment.price.dto.response;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;

import java.util.List;
import java.util.stream.Collectors;

public record PriceInfoDto(
        String name,
        Long timestamp,
        List<String> price
) {
    public static PriceInfoDto of(String name,Long timestamp, List<String> price) {
        return new PriceInfoDto(name, timestamp, price);
    }

    public static PriceInfoDto fromRedis(List<PriceInfoRedis> priceInfoRedis) {
        return new PriceInfoDto(
                priceInfoRedis.stream().findFirst().get().getName(),
                priceInfoRedis.stream().findFirst().get().getTimestamp(),
                priceInfoRedis.stream()
                        .map(PriceInfoRedis::getPrice)
                        .collect(Collectors.toList())
        );
    }
}
