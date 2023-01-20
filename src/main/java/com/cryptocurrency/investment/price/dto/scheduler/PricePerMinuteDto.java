package com.cryptocurrency.investment.price.dto.scheduler;

import lombok.Data;

import java.util.HashMap;

@Data
public class PricePerMinuteDto {
    private HashMap<String,PricePerMinuteDataDto> priceHashMap;
}
