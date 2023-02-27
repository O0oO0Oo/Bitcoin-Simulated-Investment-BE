package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.user.dto.request.UserEmailDto;
import com.cryptocurrency.investment.user.dto.request.UserAccountDto;
import com.cryptocurrency.investment.user.dto.request.UsernameDto;
import com.cryptocurrency.investment.user.service.EmailValidationService;
import com.cryptocurrency.investment.user.service.UserJoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserJoinController {

    private final UserJoinService userJoinService;
    private final EmailValidationService validationService;

    /**
     * 인증메일 보내기
     * 바인딩 에러 -> 등록된 이매일인지 -> 메일을 보낸지 2분내일떄 -> 메일 첫요청/재요청 -> MailException
     */
    @PostMapping("/email")
    public @ResponseBody ResponseWrapperDto validationMailSend(@RequestBody @Valid UserEmailDto emailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (userJoinService.findEmailByEmail(emailDto)) {
            return ResponseWrapperDto.of(ResponseStatus.USER_EMAIL_UNAVAILABLE);
        }

        Integer time = validationService.isValidationSent(emailDto);
        if (time != null && time > 0) {
            return ResponseWrapperDto.of(time, ResponseStatus.USER_EMAIL_VALIDATION_ALREADY_SENT);
        }

        try {
            if(time == null){
                validationService.saveValidation(emailDto, validationService.sendEmail(emailDto));
            } else {
                validationService.updateValidation(emailDto, validationService.sendEmail(emailDto));
            }
            return ResponseWrapperDto.of(emailDto, ResponseStatus.USER_EMAIL_VALIDATION_SENT);
        } catch (MailException e) {
            return ResponseWrapperDto.of(ResponseStatus.USER_EMAIL_VALIDATION_SENT_FAILED);
        }
    }

    /**
     * 바인딩 에러 -> 등록된 이메일인지
     */
    @GetMapping("/email")
    public @ResponseBody ResponseWrapperDto emailDetails(@RequestBody @Valid UserEmailDto emailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (userJoinService.findEmailByEmail(emailDto)) {
            return ResponseWrapperDto.of(ResponseStatus.USER_EMAIL_UNAVAILABLE);
        } else {
            return ResponseWrapperDto.of(ResponseStatus.USER_EMAIL_AVAILABLE);
        }
    }

    /**
     * 바인딩 에러 -> 등록된 이름인지
     */
    @GetMapping("/username")
    public @ResponseBody ResponseWrapperDto userDetails(@RequestBody @Valid UsernameDto usernameDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (userJoinService.findUserByUsername(usernameDto)) {
            return ResponseWrapperDto.of(ResponseStatus.USER_USERNAME_UNAVAILABLE);
        } else {
            return ResponseWrapperDto.of(ResponseStatus.USER_USERNAME_AVAILABLE);
        }
    }

    /**
     * 바인딩 에러 -> 인증코드 확인(이메일. 인증코드) -> 오래된 인증코드인지 확인 -> 등록된 이름인지
     */
    @PostMapping
    public @ResponseBody ResponseWrapperDto userAdd(@RequestBody @Valid UserAccountDto joinDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (!validationService.isCorrectValidation(joinDto)) {
            return ResponseWrapperDto.of(ResponseStatus.USER_JOIN_VALIDATION_FAIL);
        }

        if (validationService.isValidationSent(joinDto) == null){
            return ResponseWrapperDto.of(ResponseStatus.USER_JOIN_VALIDATION_CODE_EXPIRED);
        }

        if (userJoinService.findUserByUsername(joinDto)){
            return ResponseWrapperDto.of(ResponseStatus.USER_USERNAME_UNAVAILABLE);
        }

        return ResponseWrapperDto.of(userJoinService.saveUser(joinDto), ResponseStatus.USER_JOIN_SUCCEED);
    }

    /**
     * TODO: 여러곳에 적용된다 AOP 활용해보기!
     */
    public String fieldError(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(
                FieldError::getField
        ).collect(Collectors.joining(", "));
    }
}
