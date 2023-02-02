package com.cryptocurrency.investment.auth.service;

import com.cryptocurrency.investment.auth.dto.AuthenticationRequestDto;
import com.cryptocurrency.investment.auth.dto.AuthenticationResponseDto;
import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.user.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthenticationResponseDto userLogin(AuthenticationRequestDto authenticationRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getEmail(), authenticationRequestDto.getPassword())
        );
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userAccount);
        AuthenticationResponseDto response = AuthenticationResponseDto.of(userAccount.getEmail(), accessToken);
        return response;
    }
}
