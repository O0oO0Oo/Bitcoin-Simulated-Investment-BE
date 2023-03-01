package com.cryptocurrency.investment.crypto.dto.response;

import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.domain.FavoriteCrypto;

import java.util.List;

public record FavoriteResponseDto(
        String name,
        CryptoStatus status
) {
    static public FavoriteResponseDto of(String name, CryptoStatus status) {
        return new FavoriteResponseDto(name, status);
    }
}
