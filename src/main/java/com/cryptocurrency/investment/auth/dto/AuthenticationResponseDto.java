package com.cryptocurrency.investment.auth.dto;

public record AuthenticationResponseDto(
     String accessToken
){
    public static AuthenticationResponseDto of(String accessToken) {
        return new AuthenticationResponseDto(accessToken);
    }
}
