package com.cryptocurrency.investment.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequestDto(
        @NotNull
        @Email
        String email,
        @NotNull
        String password
){
    public String getEmail() {
        return email;
    }
    public String getPassword(){
        return password;
    }
}
