package com.cryptocurrency.investment.transaction.dto.request;

import com.cryptocurrency.investment.transaction.domain.TransactionType;
import lombok.Data;

public record ReservedTransactionRequestDto(
        String name,
        double price,
        double amount
) {
    public static ReservedTransactionRequestDto of(String name, double price, double amount) {
        return new ReservedTransactionRequestDto(name, price, amount);
    }
}
