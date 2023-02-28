package com.cryptocurrency.investment.transaction.repository;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAccount_Id(UUID id);

    List<Transaction> findByUserAccount_IdAndName(UUID id, String name);
}
