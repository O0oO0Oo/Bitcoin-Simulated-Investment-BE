package com.cryptocurrency.investment.price.util;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoInnerDto;
import com.cryptocurrency.investment.price.dto.scheduler.PriceInfoDto;

import java.util.concurrent.ConcurrentHashMap;

public interface PriceMessageQueue {
    public void produce(ConcurrentHashMap<String, RequestPriceInfoInnerDto> concurrentHashMap, Long timestamp);
    public PriceInfoDto consume(String name);
}
