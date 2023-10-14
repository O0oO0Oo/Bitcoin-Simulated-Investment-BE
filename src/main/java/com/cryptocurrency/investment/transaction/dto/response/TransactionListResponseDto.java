package com.cryptocurrency.investment.transaction.dto.response;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.domain.TransactionType;

import java.time.LocalDateTime;

public record TransactionListResponseDto(
        Long txId,
        String name,
        double price,
        double amount,
        TransactionStatus status,
        TransactionType type,
        Long timestamp
) {
    public static TransactionListResponseDto of(Transaction tx) {
        return new TransactionListResponseDto(
                tx.getId(),
                tx.getName(),
                tx.getPrice(),
                tx.getAmount(),
                tx.getStatus(),
                tx.getType(),
                tx.getTimestamp()
        );
    }
}
