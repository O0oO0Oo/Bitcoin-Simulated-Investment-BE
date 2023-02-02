package com.cryptocurrency.investment.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserJoinDto(
        @NotNull
        @Pattern(regexp = "^[가-힣a-zA-Z]{6,20}$")
        String username,
        @NotNull
        @Email
        String email,
        @NotNull
        @Pattern(regexp = "^(?=.*[a-z].*)(?=.*[A-Z].*)(?=.*[0-9].*)(?=.*[!@#$%^&*].*).{8,}$")
        String password,
        @NotNull
        String validation
) {
    public static UserJoinDto of(String username, String email, String password, String validation) {
        return new UserJoinDto(username, email, password, validation);
    }
}