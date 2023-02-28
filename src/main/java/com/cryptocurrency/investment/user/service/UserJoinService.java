package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.dto.request.UserEmailDto;
import com.cryptocurrency.investment.user.dto.request.UserAccountDto;
import com.cryptocurrency.investment.user.dto.request.UsernameDto;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserJoinService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public String saveUser(UserAccountDto joinDto) {
        UserAccount user = new UserAccount();
        user.setEmail(joinDto.email());
        user.setUsername(joinDto.username());
        user.setPassword(passwordEncoder.encode(joinDto.password()));
        user.setId(
                UUID.nameUUIDFromBytes((user.getEmail() + user.getUsername()).getBytes())
        );
        return userRepository.save(user).getEmail();
    }

    public boolean findEmailByEmail(UserEmailDto emailDto) {
        return userRepository.existsByEmail(emailDto.email().toLowerCase());
    }


    public boolean findUserByUsername(UsernameDto usernameDto) {
        return userRepository.existsByUsername(usernameDto.username().toLowerCase());
    }

    public boolean findUserByUsername(UserAccountDto userAccountDto) {
        return userRepository.existsByUsername(userAccountDto.username().toLowerCase());
    }
}
