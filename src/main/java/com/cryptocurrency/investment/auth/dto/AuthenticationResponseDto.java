package com.cryptocurrency.investment.auth.dto;

public record AuthenticationResponseDto(
    String email,
     String accessToken
){
    static public AuthenticationResponseDto of(String email, String accessToken) {
        return new AuthenticationResponseDto(email, accessToken);
    }
}
