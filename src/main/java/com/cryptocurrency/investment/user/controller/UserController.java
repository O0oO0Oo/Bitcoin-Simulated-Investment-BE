package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public @ResponseBody Optional<UserAccount> info(HttpServletRequest request){
        return userService.userInfo(request.getHeader("Authorization").replace("Bearer ", ""));
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
