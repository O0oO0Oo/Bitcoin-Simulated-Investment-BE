package com.cryptocurrency.investment.crypto.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FavoriteRequestDto(
        @NotEmpty
        @NotNull
        List<String> names
) {
}