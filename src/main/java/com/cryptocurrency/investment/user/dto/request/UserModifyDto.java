package com.cryptocurrency.investment.user.dto.request;

import jakarta.validation.constraints.Pattern;

public record UserModifyDto(
        @Pattern(regexp = "^[가-힣a-zA-Z]{6,20}$")
        String username,
        @Pattern(regexp = "^(?=.*[a-z].*)(?=.*[A-Z].*)(?=.*[0-9].*)(?=.*[!@#$%^&*].*).{8,}$")
        String password
) {
    static public UserModifyDto of(String username, String password) {
        return new UserModifyDto(username, password);
    }
}
