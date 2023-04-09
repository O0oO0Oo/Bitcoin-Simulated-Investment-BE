package com.cryptocurrency.investment.price.Service;

import com.cryptocurrency.investment.price.dto.response.ResponsePriceInfoDto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@RequiredArgsConstructor
@Transactional
@Service
public class PriceInfoService {

    private final PriceInfoRedisRepository redisRepository;
    private final PriceInfoMysqlRepository mysqlRepository;
    private HashMap<String,Long> intervalUnitConverter;

    @PostConstruct
    public void init(){
        intervalUnitConverter = new HashMap<>();
        intervalUnitConverter.put("m", 6L);
        intervalUnitConverter.put("h", 360L);
        intervalUnitConverter.put("d", 8640L);
    }

    public ResponsePriceInfoDto redisFindPrice(String name, Long interval) {
        return ResponsePriceInfoDto.fromRedis(
                redisRepository.findByName(name),
                name,
                interval
        );
    }

    public ResponsePriceInfoDto mysqlFindPrice(String name, Long interval, String unit){
        return ResponsePriceInfoDto.fromMysql(
                mysqlRepository.findByTimestampInterval(name.toUpperCase(),interval * intervalUnitConverter.get(unit)),
                interval,
                unit
        );
    }
}