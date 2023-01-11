package com.cryptocurrency.investment.user.controller;


import com.cryptocurrency.investment.user.validation.SaveCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/{id}")
    public String addUser(@Validated(SaveCheck.class) @PathVariable String id) {

        return "ok";
    }
}
