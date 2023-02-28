package com.cryptocurrency.investment.transaction.dto.response;

import com.cryptocurrency.investment.transaction.domain.Transaction;

import java.time.LocalDateTime;

public record TransactionResponseDto(
        String name,
        double price,
        double amount,
        LocalDateTime timestamp
) {
    public static TransactionResponseDto of(Transaction tx) {
        return new TransactionResponseDto(
                tx.getName(),
                tx.getPrice(),
                tx.getAmount(),
                tx.getTimestamp()
        );
    }
}
