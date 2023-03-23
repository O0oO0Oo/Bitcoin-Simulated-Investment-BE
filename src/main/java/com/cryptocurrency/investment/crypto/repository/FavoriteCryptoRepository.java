package com.cryptocurrency.investment.crypto.repository;

import com.cryptocurrency.investment.crypto.domain.FavoriteCrypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FavoriteCryptoRepository extends JpaRepository<FavoriteCrypto, Long> {
    List<FavoriteCrypto> findByUserAccount_Id(UUID id);
    @Query(value = "SELECT *" +
            "FROM (SELECT * FROM favorite_crypto WHERE user_account_id = :id " +
            ") as fc " +
            "WHERE fc.name in :names ", nativeQuery = true)
    List<FavoriteCrypto> findByUserAccount_IdAndNameIn(
            @Param("id") UUID id,
            @Param("names") List<String> names
    );
}
