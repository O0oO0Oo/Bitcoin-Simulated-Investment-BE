package com.cryptocurrency.investment.price.util;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoInnerDto;
import com.cryptocurrency.investment.price.dto.scheduler.PriceInfoDto;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PriceMessageBlockingQueue implements PriceMessageQueue{
    private ConcurrentHashMap<String, PriorityBlockingQueue<PriceInfoDto>> priceConcurrentHashMap
            = new ConcurrentHashMap<>();

    /**
     * Produce
     *
     * @param concurrentHashMap 매핑한 가격정보
     * @param timestamp         API 로 받은 데이터들의 timestamp
     *                          TODO : 스레드 세이프하게 변경
     */
    public void produce(ConcurrentHashMap<String, RequestPriceInfoInnerDto> concurrentHashMap, Long timestamp) {
        for (String s : concurrentHashMap.keySet()) {
            priceConcurrentHashMap.computeIfAbsent(
                    s.toUpperCase(),
                    k -> new PriorityBlockingQueue<>(60, PriceInfoDto.timestampComparator)
            );

            priceConcurrentHashMap.computeIfPresent(
                    s.toUpperCase(),
                    (k, v) -> {
                        v.put(
                                new PriceInfoDto(
                                        k.toUpperCase(),
                                        concurrentHashMap.get(s).getClosing_price(),
                                        timestamp
                                )
                        );
                        return v;
                    }
            );
        }
    }


    /**
     * @param name 소비하려는 코인의 이름
     * @return ConcurrentHashMap 의 key 에 name 이 없다면 null, 키에 name 은 있지만 Price 정보가 비어있다면 null,
     * name 이 있고 Price 정보가 있다면 시간은 다르지만 가격이 연속된 중복값이 아닐때까지 최대 n초의 가격 정보를 받아옴
     * TODO : 현재 문제점 priceInfoDtoPriorityBlockingQueue 에 여러 스레드가 접근하면 읽기전에 변경되기 때문에 스레드 세이프하지 않음. 스레드 세이프하게 변경
     */
    ThreadLocal<PriceInfoDto> local = new ThreadLocal<>();
    public PriceInfoDto consume(String name) {
        // 키가 없다면 null
        if (!priceConcurrentHashMap.containsKey(name.toUpperCase())) {
            return null;
        }
        try {
            priceConcurrentHashMap.computeIfPresent(name,
                    (k, v) ->
                    {
                        PriceInfoDto poll = v.poll();
                        Long timestamp = poll.getTimestamp();
                        while (!v.isEmpty()) {
                            PriceInfoDto peek = v.peek();
                            if (peek.getPrice().equals(poll.getPrice()) &&
                                    ((peek.getTimestamp() - timestamp) < 1000)
                            ) {
                                poll.setTimestamp(v.poll().getTimestamp());
                            } else {
                                break;
                            }
                        }
                        local.set(poll);
                        return v;
                    }
            );
            return local.get();
        }
        finally {
            local.remove();
        }
    }

    // Queue Size
    public int size(String name) {
        if (!priceConcurrentHashMap.containsKey(name.toUpperCase())) {
            return 0;
        }

        return priceConcurrentHashMap.get(name.toUpperCase()).size();
    }
}