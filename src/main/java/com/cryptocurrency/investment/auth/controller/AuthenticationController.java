package com.cryptocurrency.investment.auth.controller;

import com.cryptocurrency.investment.auth.dto.AuthenticationResponseDto;
import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtUtils jwtUtils;

    /**
     * 로그인 성공/실패
     */    
    @PostMapping("/login")
    public @ResponseBody ResponseWrapperDto authDetail(Authentication authentication){
        if (authentication == null) {
            return ResponseWrapperDto.of(ResponseStatus.USER_LOGIN_FAILED);
        }
        String token = jwtUtils.generateAccessToken(authentication);
        return ResponseWrapperDto.of(
                ResponseStatus.USER_LOGIN_SUCCEED,
                AuthenticationResponseDto.of(token)
        );
    }
}