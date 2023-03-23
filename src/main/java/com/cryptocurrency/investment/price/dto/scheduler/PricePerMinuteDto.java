package com.cryptocurrency.investment.price.dto.scheduler;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class PricePerMinuteDto {
    private ConcurrentHashMap<String,PricePerMinuteDataDto> priceHashMap;
}