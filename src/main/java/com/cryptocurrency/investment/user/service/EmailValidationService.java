package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.EmailValidation;
import com.cryptocurrency.investment.user.dto.request.UserEmailDto;
import com.cryptocurrency.investment.user.dto.request.UserJoinDto;
import com.cryptocurrency.investment.user.repository.EmailValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailValidationService {
    @Value("${mail.validation.resend}")
    private int RESEND;
    @Value("${mail.validation.expire}")
    private int EXPIRE;
    private final JavaMailSender mailSender;
    private final EmailValidationRepository validationRepository;

    public Integer sendEmail(UserEmailDto emailDto) {
        Random random = new Random();
        Integer code = random.nextInt(900000) + 100000;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.email());
        message.setSubject("Hello!");
        message.setText("Validation Code : " + code);
        mailSender.send(message);
        return code;
    }


    public EmailValidation saveValidation(UserEmailDto emailDto, Integer code) {
        return validationRepository.save(new EmailValidation(emailDto.email(), code));
    }

    public Integer isValidationSent(UserEmailDto emailDto) {
        return validationRepository.findByEmailSent(emailDto.email(), RESEND);
    }

    public Integer isValidationSent(UserJoinDto joinDto) {
        return validationRepository.findByEmailSent(joinDto.email(), EXPIRE);
    }

    /**
     * 메일, 인증번호를 검증
     * @param joinDto
     */
    public boolean isCorrectValidation(UserJoinDto joinDto) {
        return validationRepository.existsByEmailAndCode(joinDto.email(), joinDto.code());
    }

    /**
     *  인증 완료시 데이터 삭제
     * @param emailDto
     * @return 성공 1, 실패 0
     */
    public int deleteValidation(UserEmailDto emailDto) {
        return validationRepository.deleteByEmail(emailDto.email());
    }

    /**
     * 메일 재전송시 인증번호 갱신
     * @param emailDto
     * @return 성공 1, 실패 0
     */
    public int updateValidation(UserEmailDto emailDto, Integer code) {
        return validationRepository.updateByEmail(emailDto.email(), code);
    }
}
