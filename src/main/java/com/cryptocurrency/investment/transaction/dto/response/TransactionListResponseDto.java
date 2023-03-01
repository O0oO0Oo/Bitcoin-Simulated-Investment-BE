package com.cryptocurrency.investment.transaction.dto.response;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;

import java.time.LocalDateTime;

public record TransactionListResponseDto(
        String name,
        double price,
        double amount,
        TransactionStatus status,
        LocalDateTime timestamp
) {
    public static TransactionListResponseDto of(Transaction tx) {
        return new TransactionListResponseDto(
                tx.getName(),
                tx.getPrice(),
                tx.getAmount(),
                tx.getStatus(),
                tx.getTimestamp()
        );
    }
}
