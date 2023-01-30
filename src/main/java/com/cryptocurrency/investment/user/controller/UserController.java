package com.cryptocurrency.investment.user.controller;


import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.dto.UserSignUpDto;
import com.cryptocurrency.investment.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{email}")
    public @ResponseBody boolean checkEmail(@PathVariable("email") String email){
        return userService.isExistEmail(email);
    }
    @GetMapping("/{username}")
    public @ResponseBody boolean checkUsername(@PathVariable("username") String username){
        return userService.isExistUsername(username);
    }

    @GetMapping
    public @ResponseBody Optional<UserAccount> info(HttpServletRequest request){
        return userService.userInfo(request.getHeader("Authorization").replace("Bearer ", ""));
    }

    /**
     * @param userSignUpDto Request 유저 Dto
     * @return 성공시 가입시 기입한 username 반환
     */
    @PostMapping
    public @ResponseBody String signUp(@RequestBody @Validated UserSignUpDto userSignUpDto) {
        return userService.userSave(userSignUpDto.username(), userSignUpDto.email(),userSignUpDto.password());
    }

    @GetMapping("/attendance")
    public @ResponseBody HashSet<LocalDate> getAttendance(HttpServletRequest request){
        return userService.getUserAttendance(request.getHeader("Authorization").replace("Bearer ", ""));
    }

    @PostMapping("/attendance")
    public @ResponseBody HashSet<LocalDate> attendance(HttpServletRequest request){
        return userService.userAttendance(request.getHeader("Authorization").replace("Bearer ", ""));
    }
}
