package com.cryptocurrency.investment.Service.redis;

import com.cryptocurrency.investment.domain.redis.CurrencyPriceRedis;
import com.cryptocurrency.investment.repository.redis.CurrencyPriceInfoRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyPriceRedisService {

    private CurrencyPriceInfoRedisRepository redisRepository;

    @Autowired
    public CurrencyPriceRedisService(CurrencyPriceInfoRedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public void savePriceInfo(CurrencyPriceRedis priceRedis) {
        redisRepository.save(priceRedis);
    }

    public CurrencyPriceRedis getPriceInfo(Long id) {
        return redisRepository.findById(id).orElse(null);
    }
}
