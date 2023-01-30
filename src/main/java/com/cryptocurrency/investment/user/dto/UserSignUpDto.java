package com.cryptocurrency.investment.user.dto;

import com.cryptocurrency.investment.user.domain.UserAccount;

import java.util.Optional;

public record UserSignUpDto(
        String username,
        String email,
        String password
) {
    public static UserSignUpDto of(String username, String email, String password) {
        return new UserSignUpDto(username, email, password);
    }

    public static UserSignUpDto from(Optional<UserAccount> userAccount) {
        return new UserSignUpDto(userAccount.get().getUsername(), userAccount.get().getEmail(), userAccount.get().getPassword());
    }
}
