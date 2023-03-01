package com.cryptocurrency.investment.crypto.repository;

import com.cryptocurrency.investment.crypto.domain.FavoriteCrypto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteCryptoRepository extends JpaRepository<FavoriteCrypto, Long> {
    List<FavoriteCrypto> findByUserAccount_Id(UUID id);
    boolean deleteByUserAccount_IdAndNameIn(UUID id, List<String> names);
}
