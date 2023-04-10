package com.cryptocurrency.investment.transaction.repository;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAccount_Id(UUID id);

    List<Transaction> findByUserAccount_IdAndName(UUID id, String name);

    @Query(value = "" +
            "SELECT * " +
            "FROM transaction t " +
            "WHERE t.user_account_id = :userId " +
            "AND (t.type = 'RESERVE_BUY' OR t.type = 'RESERVE_SELL') ", nativeQuery = true)
    List<Transaction> findAllReservedTxByUserAccount_Id(@Param("userId") UUID id);

    @Query("" +
            "SELECT t " +
            "FROM Transaction t " +
            "WHERE t.id IN :ids AND t.userAccount.id = :userId " +
            "AND (t.type = 'RESERVE_BUY' OR t.type = 'RESERVE_SELL')")
    List<Transaction> findAllReservedTxByIdsAndUserAccount_Id(
            @Param("ids") List<Long> ids,
            @Param("userId") UUID userId
    );
    @Modifying
    @Transactional
    @Query("" +
            "DELETE " +
            "FROM Transaction t " +
            "where t.id IN :ids AND t.userAccount.id = :userId " +
            "and (t.type = 'RESERVE_BUY' OR t.type = 'RESERVE_SELL')")
    int deleteAllReservedTxByIdAndUserAccount_Id(
            @Param("ids") List<Long> ids,
            @Param("userId") UUID userId);
}
