package com.cryptocurrency.investment.user.controller;


import com.cryptocurrency.investment.user.dto.UserSignUpDto;
import com.cryptocurrency.investment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * @param userSignUpDto Request 유저 Dto
     * @return 성공시 가입시 기입한 username 반환
     */
    @PostMapping
    public @ResponseBody String signUp(@RequestBody @Validated UserSignUpDto userSignUpDto) {
        return userService.userSave(userSignUpDto.username(), userSignUpDto.email(),userSignUpDto.password());
    }

    @GetMapping("/attendance")
    public @ResponseBody String attendance(){
        return LocalDateTime.now().toString();
    }
}
