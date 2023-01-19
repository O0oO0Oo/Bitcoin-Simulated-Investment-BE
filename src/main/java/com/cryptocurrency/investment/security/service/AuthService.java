package com.cryptocurrency.investment.security.service;

import com.cryptocurrency.investment.security.dto.AuthResponse;
import com.cryptocurrency.investment.security.jwt.JwtUtils;
import com.cryptocurrency.investment.user.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    public ResponseEntity<?> userLogin(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserAccount userAccount = (UserAccount) authentication.getPrincipal();
            System.out.println("userAccount.getUsername() + userAccount.getEmail() = " + userAccount.getUsername() + userAccount.getEmail());
            String accessToken = jwtUtils.generateAccessToken(userAccount);
            AuthResponse response = new AuthResponse(userAccount.getEmail(), accessToken);
            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
