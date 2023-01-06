package com.cryptocurrency.investment.repository.redis;


import com.cryptocurrency.investment.domain.redis.CurrencyPriceRedis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyPriceInfoRedisRepository extends JpaRepository<CurrencyPriceRedis, Long > {
}
