package com.cryptocurrency.investment.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserEmailDto(
        @NotNull
        @Email
        String email
) {
}