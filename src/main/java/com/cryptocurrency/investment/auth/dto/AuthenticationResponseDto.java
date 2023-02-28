package com.cryptocurrency.investment.auth.dto;

public record AuthenticationResponseDto(
     String accessToken
){
    static public AuthenticationResponseDto of(String accessToken) {
        return new AuthenticationResponseDto(accessToken);
    }
}
