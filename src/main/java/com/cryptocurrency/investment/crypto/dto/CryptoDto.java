package com.cryptocurrency.investment.crypto.dto;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import jakarta.validation.constraints.NotNull;

public record CryptoDto(
        @NotNull
        String name,
        @NotNull
        CryptoStatus status
) {
    public static CryptoDto of(Crypto crypto) {
        return new CryptoDto(crypto.getName(), crypto.getStatus());
    }
}
