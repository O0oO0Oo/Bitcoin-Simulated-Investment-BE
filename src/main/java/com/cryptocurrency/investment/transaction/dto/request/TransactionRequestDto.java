package com.cryptocurrency.investment.transaction.dto.request;

import com.cryptocurrency.investment.transaction.domain.TransactionType;

public record TransactionRequestDto(
        String name,
        double price,
        double amount,
        TransactionType type
) {
}
