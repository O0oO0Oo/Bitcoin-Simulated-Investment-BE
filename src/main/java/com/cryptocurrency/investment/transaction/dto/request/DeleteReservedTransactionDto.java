package com.cryptocurrency.investment.transaction.dto.request;

import java.util.List;

public record DeleteReservedTransactionDto(
        List<Long> id
) {
    public static DeleteReservedTransactionDto of(List<Long> id) {
        return new DeleteReservedTransactionDto(id);
    }
}
