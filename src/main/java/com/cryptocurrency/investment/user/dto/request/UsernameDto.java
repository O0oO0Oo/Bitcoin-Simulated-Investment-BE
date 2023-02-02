package com.cryptocurrency.investment.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UsernameDto(
        @NotNull
        @Pattern(regexp = "^[가-힣a-zA-Z]{6,20}$")
        String username
) {
}