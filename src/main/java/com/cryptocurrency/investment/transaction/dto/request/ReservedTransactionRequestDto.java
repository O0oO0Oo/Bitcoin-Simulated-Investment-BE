package com.cryptocurrency.investment.transaction.dto.request;

import com.cryptocurrency.investment.transaction.domain.TransactionType;
import lombok.Data;

public record ReservedTransactionRequestDto(
        String name,
        double price,
        double amount,
        TransactionType type
) {
    public static ReservedTransactionRequestDto of(String name, double price, double amount, TransactionType type) {
        return new ReservedTransactionRequestDto(name, price, amount, type);
    }
}
