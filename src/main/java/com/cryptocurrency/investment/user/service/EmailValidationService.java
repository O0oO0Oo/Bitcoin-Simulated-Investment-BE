package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.EmailValidation;
import com.cryptocurrency.investment.user.dto.request.UserEmailDto;
import com.cryptocurrency.investment.user.dto.request.UserJoinDto;
import com.cryptocurrency.investment.user.repository.EmailValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailValidationService {

    @Value("${mail.validation.resend}")
    private int RESEND;

    @Value("${mail.validation.expire}")
    private int EXPIRE;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailValidationRepository validationRepository;

    public String sendEmail(UserEmailDto emailDto) {
        Random random = new Random();
        Integer randomInt = random.nextInt(1000000 - 100000);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.email());
        message.setSubject("Hello!");
        message.setText("Validation Code : " + randomInt.toString());
        mailSender.send(message);
        return randomInt.toString();
    }


    public EmailValidation saveValidation(UserEmailDto emailDto, String code) {
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
        return validationRepository.existsByEmailAndValidation(joinDto.email(), joinDto.validation());
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
    public int updateValidation(UserEmailDto emailDto, String code) {
        return validationRepository.updateByEmail(emailDto.email(), code);
    }
}
