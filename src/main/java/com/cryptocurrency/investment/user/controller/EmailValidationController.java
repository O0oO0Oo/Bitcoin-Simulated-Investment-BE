package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.user.dto.request.UserEmailDto;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class EmailValidationController {
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

        if (time != null && time <= 0) {
            Integer code = validationService.sendEmail(emailDto);
            validationService.updateValidation(emailDto, code);
            return ResponseWrapperDto.of(emailDto, ResponseStatus.USER_EMAIL_VALIDATION_SENT);
        }

        try {
            if(time == null){
                Integer code = validationService.sendEmail(emailDto);
                validationService.saveValidation(emailDto, code);
            }
            return ResponseWrapperDto.of(emailDto, ResponseStatus.USER_EMAIL_VALIDATION_SENT);
        } catch (MailException e) {
            return ResponseWrapperDto.of(ResponseStatus.USER_EMAIL_VALIDATION_SENT_FAILED);
        }
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
