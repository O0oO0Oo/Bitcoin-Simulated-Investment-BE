package com.cryptocurrency.investment.price.repository.redis;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceInfoRedisRepository extends JpaRepository<PriceInfoRedis, String> {
    List<PriceInfoRedis> findTop300ByNameOrderByTimestampDesc(String name);
    List<PriceInfoRedis> findByName(String name);
}