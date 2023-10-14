package com.cryptocurrency.investment.transaction.dto.response;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.user.domain.UserAccount;
import org.apache.catalina.User;

import java.time.LocalDateTime;

public record TransactionResponseDto(
        String name,
        double price,
        double amount,
        TransactionType type,
        Long timestamp,
        double money
) {
    static public TransactionResponseDto of(Transaction tx, UserAccount user) {
        return new TransactionResponseDto(
                tx.getName(),
                tx.getPrice(),
                tx.getAmount(),
                tx.getType(),
                tx.getTimestamp(),
                user.getMoney()
        );
    }
}
