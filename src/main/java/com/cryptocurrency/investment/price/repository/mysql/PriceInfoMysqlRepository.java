package com.cryptocurrency.investment.price.repository.mysql;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceInfoMysqlRepository extends JpaRepository<PriceInfoMysql, String> {
    @Query(value = "SELECT * " +
            "FROM price_info_mysql as p " +
            "WHERE p.name = :name and " +
            "mod(p.timestamp/10000+3240,:interval) = 0 " +
            "ORDER BY p.timestamp DESC",nativeQuery = true)
    List<PriceInfoMysql> findByTimestampInterval(
            @Param("name") String name,
            @Param("interval") Long interval
    );
}