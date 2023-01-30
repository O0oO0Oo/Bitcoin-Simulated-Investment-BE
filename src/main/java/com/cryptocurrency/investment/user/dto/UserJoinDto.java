package com.cryptocurrency.investment.user.dto;

public record UserJoinDto(
        String username,
        String email,
        String password,

        String validation
) {
    public static UserJoinDto of(String username, String email, String password, String validation) {
        return new UserJoinDto(username, email, password, validation);
    }
}