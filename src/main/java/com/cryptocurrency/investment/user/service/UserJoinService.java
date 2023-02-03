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

@Service
@RequiredArgsConstructor
public class UserJoinService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public String userSave(UserAccountDto joinDto) {
        return userRepository.save(
                        new UserAccount(
                                joinDto.email(),
                                joinDto.username(),
                                passwordEncoder.encode(joinDto.password()),
                                Role.USER)
                )
                .getUsername();
    }

    public boolean isExistEmail(UserEmailDto emailDto) {
        return userRepository.existsByEmail(emailDto.email());
    }


    public boolean isExistUsername(UsernameDto usernameDto) {
        return userRepository.existsByUsername(usernameDto.username());
    }

    public boolean isExistUsername(UserAccountDto userAccountDto) {
        return userRepository.existsByUsername(userAccountDto.username());
    }
}
