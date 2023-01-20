package com.cryptocurrency.investment.price.dto.response;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;
import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;

import java.util.List;
import java.util.stream.Collectors;

public record ResponsePriceInfoDto(
        String name,
        String timeInterval,
        Long timestamp,
        List<String> price
) {
    public static ResponsePriceInfoDto of(String name,String timeInterval, Long timestamp, List<String> price) {
        return new ResponsePriceInfoDto(name,timeInterval ,timestamp, price);
    }

    public static ResponsePriceInfoDto fromRedis(List<PriceInfoRedis> priceInfoRedis) {
        return new ResponsePriceInfoDto(
                priceInfoRedis.stream().findFirst().get().getName(),
                "1s",
                priceInfoRedis.stream().findFirst().get().getTimestamp(),
                priceInfoRedis.stream()
                        .map(PriceInfoRedis::getPrice)
                        .collect(Collectors.toList())
        );
    }

    public static ResponsePriceInfoDto fromMysql(List<PriceInfoMysql> priceInfoMysql, Long timeInterval, String unit) {
        return new ResponsePriceInfoDto(
                priceInfoMysql.stream().findFirst().get().getName(),
                timeInterval + unit,
                priceInfoMysql.stream().findFirst().get().getTimestamp(),
                priceInfoMysql.stream()
                        .map(PriceInfoMysql::getPrice)
                        .collect(Collectors.toList())
        );
    }
}
