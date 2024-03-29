package com.cryptocurrency.investment.transaction.dto.request;

import com.cryptocurrency.investment.transaction.domain.TransactionType;
import jakarta.validation.constraints.Min;
import org.jetbrains.annotations.Range;

public record TransactionRequestDto(
        String name,
        double amount,
        TransactionType type
) {
}
