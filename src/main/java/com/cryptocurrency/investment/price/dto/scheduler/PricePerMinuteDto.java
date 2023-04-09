package com.cryptocurrency.investment.price.dto.scheduler;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import lombok.Data;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class PricePerMinuteDto {
    private ConcurrentHashMap<String,PricePerMinuteDataDto> priceHashMap;

    public void setPriceHashMap(ConcurrentHashMap<String, PricePerMinuteDataDto> priceHashMap) {
        this.priceHashMap = priceHashMap;
    }

    public void setPriceHashMap(RequestPriceInfoDto priceInfoDto) {
        priceInfoDto.getFields().forEach((k, v) -> {
            if (!this.priceHashMap.containsKey(k)) {
                this.priceHashMap.put(
                        k,
                        new PricePerMinuteDataDto(
                                v.getClosing_price()
                        )
                );
            } else {
                this.priceHashMap.get(k)
                        .setPrice(v.getClosing_price());
            }
        });
    }

    public List<PriceInfoMysql> getPriceInfoMysqlList() {
        long currentTimeMillis = System.currentTimeMillis();
        return this.priceHashMap.entrySet().stream()
                .map(entry -> new PriceInfoMysql(
                                entry.getKey(),
                        currentTimeMillis - currentTimeMillis % 1000,
                                Double.parseDouble(entry.getValue().getCurPrice()),
                                Double.parseDouble(entry.getValue().getMaxPrice()),
                                Double.parseDouble(entry.getValue().getMinPrice())
                        )
                )
                .collect(Collectors.toList());
    }
}