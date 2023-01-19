package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public String userSave(String username, String email, String password) {
        return userRepository.save(new UserAccount(username, email, passwordEncoder.encode(password), Role.USER, LocalDateTime.now())).getUsername();
    }

}
