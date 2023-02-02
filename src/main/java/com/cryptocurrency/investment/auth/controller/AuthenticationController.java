package com.cryptocurrency.investment.auth.controller;

import com.cryptocurrency.investment.auth.dto.AuthenticationRequestDto;
import com.cryptocurrency.investment.auth.service.AuthenticationService;
import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    /**
     * 바인딩 에러 -> 로그인 성공/실패
     */    
    @PostMapping("/login")
    public @ResponseBody ResponseWrapperDto login(@RequestBody @Valid AuthenticationRequestDto authenticationRequestDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        try {
            return ResponseWrapperDto.of(ResponseStatus.USER_LOGIN_SUCCEED, authenticationService.userLogin(authenticationRequestDto));
        } catch(BadCredentialsException e) {
            return ResponseWrapperDto.of(ResponseStatus.USER_LOGIN_FAILED);
        }
    }

    /**
     * TODO: AOP
     */
    public String fieldError(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(
                FieldError::getField
        ).collect(Collectors.joining(", "));
    }
}