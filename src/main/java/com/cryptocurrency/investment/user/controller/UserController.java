package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.user.dto.request.UserModifyDto;
import com.cryptocurrency.investment.user.dto.response.UserGetDto;
import com.cryptocurrency.investment.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 조회 성공/실패
     */
    @GetMapping
    public @ResponseBody ResponseWrapperDto userInfo(HttpServletRequest request){
        return userService.getUserInfo(request.getHeader("Authorization").replace("Bearer ", ""))
                .map(
                        userInfo -> ResponseWrapperDto.of(ResponseStatus.USER_INFO_GET_SUCCEED, UserGetDto.of(userInfo))
                ).orElse(
                        ResponseWrapperDto.of(ResponseStatus.USER_INFO_GET_FAILED)
                );
    }

    /**
     * 바인딩 에러 -> 등록된 이름 -> PUT/GET 성공/실패
     */
    @PutMapping
    public @ResponseBody ResponseWrapperDto userModify(@RequestBody @Valid UserModifyDto modifyDto,
                                                       BindingResult bindingResult,
                                                       HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (userService.isExistUsername(modifyDto)) {
            return ResponseWrapperDto.of(ResponseStatus.USER_USERNAME_UNAVAILABLE);
        }

        String token = request.getHeader("Authorization").replace("Bearer ", "");

        if (userService.modifyUserInfo(token, modifyDto) == 1) {
            return userService.getUserInfo(request.getHeader("Authorization").replace("Bearer ", ""))
                    .map(
                            userInfo -> ResponseWrapperDto.of(ResponseStatus.USER_INFO_PUT_SUCCEED, UserGetDto.of(userInfo))
                    ).orElse(
                            ResponseWrapperDto.of(ResponseStatus.USER_INFO_PUT_SUCCEED_GET_FAILED)
                    );
        } else {
            return ResponseWrapperDto.of(ResponseStatus.USER_INFO_PUT_FAILED);
        }
    }

    /**
     * DELETE 성공/실패
     */
    @DeleteMapping
    public @ResponseBody ResponseWrapperDto userDelete(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        if (userService.deleteUserInfo(token) == 1) {
            return ResponseWrapperDto.of(ResponseStatus.USER_INFO_DELETE_SUCCEED);
        } else {
            return ResponseWrapperDto.of(ResponseStatus.USER_INFO_DELETE_FAILED);
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
