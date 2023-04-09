package com.cryptocurrency.investment.price.repository.redis;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 테스트를 제외하고 실서비스에서 사용되지 않음.
public interface PriceInfoRedisJpaRepository extends JpaRepository<PriceInfoRedis, String> {
    List<PriceInfoRedis> findTop300ByNameOrderByTimestampDesc(String name);
    List<PriceInfoRedis> findByName(String name);
}