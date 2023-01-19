package com.cryptocurrency.investment.user.dto;

public record UserSignUpDto(
        String username,
        String email,
        String password
) {
    public static UserSignUpDto of(String username, String email, String password) {
        return new UserSignUpDto(username, email, password);
    }
}
