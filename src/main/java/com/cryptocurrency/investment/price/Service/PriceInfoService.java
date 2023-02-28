package com.cryptocurrency.investment.price.Service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import com.cryptocurrency.investment.price.dto.response.ResponsePriceInfoDto;
import com.cryptocurrency.investment.price.dto.text;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

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

    public text redisFindPrice(String name) {
        return text.of(
                redisRepository.findTop300ByNameOrderByTimestampDesc(name));
    }

    public ResponsePriceInfoDto mysqlFindPrice(String name, Long interval, String unit){
        return ResponsePriceInfoDto.fromMysql(
                mysqlRepository.findByTimestampInterval(name,interval * intervalUnitConverter.get(unit)),
                interval,
                unit
        );
    }
}