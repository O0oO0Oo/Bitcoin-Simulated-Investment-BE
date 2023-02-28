package com.cryptocurrency.investment.price.repository.redis;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriceInfoRedisRepository extends JpaRepository<PriceInfoRedis, String> {
    List<PriceInfoRedis> findTop300ByNameOrderByTimestampDesc(String name);
}
