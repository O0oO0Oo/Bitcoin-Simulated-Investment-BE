package com.cryptocurrency.investment.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.Contract;

public record UserAccountDto(
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
    @Contract("_, _, _, _ -> new")
    public static @NotNull UserAccountDto of(String username, String email, String password, String validation) {
        return new UserAccountDto(username, email, password, validation);
    }
}