package com.cryptocurrency.investment.price.Service;

import com.cryptocurrency.investment.price.dto.response.ResponsePriceInfoDto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@RequiredArgsConstructor
@Transactional
@Service
public class PriceInfoService {

    @Autowired
    PriceInfoRedisRepository redisRepository;
    @Autowired
    PriceInfoMysqlRepository mysqlRepository;
    private HashMap<String,Long> intervalUnitConverter;

    @PostConstruct
    public void init(){
        intervalUnitConverter = new HashMap<>();
        intervalUnitConverter.put("m", 6L);
        intervalUnitConverter.put("h", 360L);
        intervalUnitConverter.put("d", 8640L);
    }

    public ResponsePriceInfoDto getPriceInfoRedis(String name) {
        return ResponsePriceInfoDto.fromRedis(
                redisRepository.findTop300ByNameOrderByTimestampDesc(name));
    }

    public ResponsePriceInfoDto getPriceInfoMysql(String name, Long interval, String unit){
        return ResponsePriceInfoDto.fromMysql(
                mysqlRepository.findByTimestampInterval(name,interval * intervalUnitConverter.get(unit)),
                interval,
                unit
        );
    }
}