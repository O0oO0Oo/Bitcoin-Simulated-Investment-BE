package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.EmailValidationRepository;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserJoinService {
    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailValidationRepository validationRepository;

    public String userSave(String username, String email, String password, String validation) {
        if (!validationRepository.existsByEmailAndValidation(email, validation)) {
            return "이메일과 인증번호를 확인해주세요.";
        }
        else {
            return userRepository.save(
                    new UserAccount(username, email, passwordEncoder.encode(password), Role.USER)).getUsername();
        }
    }

    public boolean isExistUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
