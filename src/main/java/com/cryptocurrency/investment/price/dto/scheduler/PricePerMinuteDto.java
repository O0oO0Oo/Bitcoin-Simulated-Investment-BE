package com.cryptocurrency.investment.price.dto.scheduler;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Data
public class PricePerMinuteDto {
    private HashMap<String,PricePerMinuteDataDto> priceHashMap;
}
