package com.cryptocurrency.investment.crypto.dto;

import com.cryptocurrency.investment.crypto.domain.CryptoStatus;

public record CryptoModifyDto(
        String name,
        String newName,
        CryptoStatus newStatus
) {
}
