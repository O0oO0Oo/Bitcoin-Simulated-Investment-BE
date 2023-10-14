package com.cryptocurrency.investment.price.repository.redis;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoInnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class PriceInfoRedisRepository {
    private final RedisConnectionFactory redisConnectionFactory;
    public void saveAll(ConcurrentHashMap<String, RequestPriceInfoInnerDto> fields, Long localDateTime) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        fields.forEach((k, v) -> {
            connection.zSetCommands().zRemRangeByScore(
                    ("price:" + k).getBytes(),
                    localDateTime, localDateTime);

            connection.zAdd(
                    ("price:" + k).getBytes(),
                    localDateTime,
                    (v.getClosing_price() + ":" + localDateTime).getBytes());
        });
        connection.closePipeline();
        connection.close();
    }

    public List<String> findByName(String name) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        List<String> data = connection.zSetCommands().zRevRange(
                ("price:" + name.toUpperCase()).getBytes(),
                0, 180
        ).stream().map( bytes -> new String(bytes)).toList();
        connection.close();
        return data;
    }

    // Pagination 을 위함
    public List<String> findByNameAndLocalDateTime(String name, Long localDateTime) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        List<String> data = connection.zSetCommands().zRevRangeByScore(
                ("price:" + name.toUpperCase()).getBytes(),
                0,
                localDateTime,
                0,
                180
        ).stream().map( bytes -> new String(bytes)).toList();
        connection.close();
        return data;
    }
}
