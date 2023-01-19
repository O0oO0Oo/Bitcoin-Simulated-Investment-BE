package com.cryptocurrency.investment.price.Service;

import com.cryptocurrency.investment.price.dto.response.PriceInfoDto;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PriceInfoService {

    @Autowired
    PriceInfoRedisRepository redisRepository;

    public PriceInfoDto getPriceInfo(String name) {
        return PriceInfoDto.fromRedis(redisRepository.findTop300ByNameOrderByTimestampDesc(name));
    }
}