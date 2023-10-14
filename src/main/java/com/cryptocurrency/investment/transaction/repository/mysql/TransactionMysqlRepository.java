package com.cryptocurrency.investment.transaction.repository.mysql;

import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJpaDto;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionMysqlRepository extends JpaRepository<Transaction, Long> {
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

    @Query(value = "" +
            "SELECT * " +
            "FROM transaction t " +
            "WHERE t.price = :price and t.status = 'RESERVED' and t.name = :name", nativeQuery = true
    )
    List<Transaction> findByNameAndPriceAndStatusIsReserved(
            @Param("name") String name,
            @Param("price") Double price
    );

    // TEST : 필요한 컬럼만 받기
    @Query(value = "" +
            "SELECT t.id, t.price, t.amount, t.name, t.user_account_id, t.type " +
            "FROM transaction t " +
            "WHERE t.price = :price and t.status = 'RESERVED' and t.name = :name",
            nativeQuery = true
    )
    List<ReservedTransactionJpaDto> findByNameAndPriceAndStatusIsReservedForDto(
            @Param("name") String name,
            @Param("price") Double price
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE Transaction t SET t.status = 'COMPLETED' WHERE t.id IN :idList", nativeQuery = true)
    void updateStatusByIdIn(@Param("idList") List<Long> idList);
}
