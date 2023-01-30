package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.user.dto.UserJoinDto;
import com.cryptocurrency.investment.user.service.EmailValidationService;
import com.cryptocurrency.investment.user.service.UserJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserJoinController {

    private final UserJoinService userJoinService;

    private final EmailValidationService emailValidationService;


    @GetMapping("/email")
    public @ResponseBody String emailValidation(@RequestBody @Validated Map<String ,String > map) {
        String email = map.get("email");
        if (userJoinService.isExistEmail(email)) {
            return "이미 등록된 email 이메일 입니다. 다른 이메일을 사용해주세요";
        }
        return emailValidationService.sendEmail(email);
    }

    @GetMapping("/username")
    public @ResponseBody boolean checkUsername(@RequestBody String username){
        return userJoinService.isExistUsername(username);
    }

    /**
     * @param userJoinDto Request 유저 Dto
     * @return 성공시 가입시 기입한 username 반환
     */
    @PostMapping
    public @ResponseBody String join(@RequestBody @Validated UserJoinDto userJoinDto) {
        return userJoinService.userSave(userJoinDto.username(), userJoinDto.email(), userJoinDto.password(), userJoinDto.validation());
    }
}
