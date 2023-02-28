package com.cryptocurrency.investment.wallet.repository;

import com.cryptocurrency.investment.wallet.domain.Wallet;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserAccount_Id(UUID id);
    Optional<Wallet> findByUserAccount_IdAndName(UUID id, String name);
}
