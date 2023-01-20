package com.cryptocurrency.investment.auth.controller;

import com.cryptocurrency.investment.auth.dto.AuthRequest;
import com.cryptocurrency.investment.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthRequest authRequest) {
        return authService.userLogin(authRequest.getEmail(),authRequest.getPassword());
    }
}
