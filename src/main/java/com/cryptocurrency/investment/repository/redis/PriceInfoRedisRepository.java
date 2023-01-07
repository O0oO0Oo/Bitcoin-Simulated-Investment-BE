package com.cryptocurrency.investment.repository.redis;


import com.cryptocurrency.investment.domain.redis.PriceInfoRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceInfoRedisRepository extends CrudRepository<PriceInfoRedis, String> {
    List<PriceInfoRedis> findByNameOrderByTimestampDesc(String name);
}
