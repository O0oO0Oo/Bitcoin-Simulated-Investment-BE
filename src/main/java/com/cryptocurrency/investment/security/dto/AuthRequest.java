package com.cryptocurrency.investment.security.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AuthRequest {

    @Email
    private String email;

    private String password;
}
