package com.cryptocurrency.investment.transaction.dto.request;

import java.util.List;

public record DeleteReservedTransactionRequestDto(
        List<Long> ids
) {
    public static DeleteReservedTransactionRequestDto of(List<Long> id) {
        return new DeleteReservedTransactionRequestDto(id);
    }
}
