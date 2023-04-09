package com.cryptocurrency.investment.price.dto.response;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;

import java.util.*;
import java.util.stream.Collectors;

public record ResponsePriceInfoDto(
        String name,
        String timeInterval,
        SortedMap<Long, Double> price
) {
    public static ResponsePriceInfoDto fromRedis(List<String> priceInfoRedis, String name, Long timeInterval) {
        return new ResponsePriceInfoDto(
                name,
                timeInterval + "s",
                priceInfoRedis.parallelStream()
                        .map(data -> {
                            String[] split = data.split(":");
                            return new AbstractMap.SimpleEntry<>(Long.parseLong(split[1]), Double.parseDouble(split[0]));
                        })
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (v1, v2) -> v1,
                                TreeMap::new)
                        )
        );
    }

    public static ResponsePriceInfoDto fromMysql(List<PriceInfoMysql> priceInfoMysql, Long timeInterval, String unit) {
        return new ResponsePriceInfoDto(
                priceInfoMysql.stream().findFirst().get().getName(),
                timeInterval + unit,
                priceInfoMysql.parallelStream()
                        .map(data -> {
                            return new AbstractMap.SimpleEntry<>(data.getTimestamp(), data.getPrice());
                        })
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (v1, v2) -> v1,
                                TreeMap::new)
                        )
        );
    }
}
