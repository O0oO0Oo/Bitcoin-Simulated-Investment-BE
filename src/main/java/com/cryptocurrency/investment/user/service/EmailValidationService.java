package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.EmailValidation;
import com.cryptocurrency.investment.user.repository.EmailValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailValidationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailValidationRepository validationRepository;

    public String sendEmail(String email) {
        if(validationRepository.existsByEmail(email)){
            return "이미 메일이 발송 되었습니다. 스팸보관함을 확인해주세요. 재요청은 1분후애 가능합니다.";
        }
        else {
            Random random = new Random();
            String randomInt = String.valueOf(random.nextInt(1000000 - 100000));
            validationRepository.save(
                    new EmailValidation(email, randomInt)
            );
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Hello!");
            message.setText("Validation Code : " + randomInt);
            mailSender.send(message);

            return "인증번호 메일이 발송 되었습니다.";
        }

    }
}
