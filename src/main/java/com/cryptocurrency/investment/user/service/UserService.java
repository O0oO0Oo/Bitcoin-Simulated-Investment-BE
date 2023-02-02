package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.dto.request.UserModifyDto;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    public Optional<UserAccount> getUserInfo(String token) {
        String email = jwtUtils.getSubject(token);
        return userRepository.findByEmail(email);
    }

    public int modifyUserInfo(String token, UserModifyDto modifyDto) {
        String email = jwtUtils.getSubject(token);
        return userRepository.updateUser(email, modifyDto.username(), modifyDto.password());
    }

    public int deleteUserInfo(String token) {
        String email = jwtUtils.getSubject(token);
        return userRepository.deleteByEmail(email);
    }

    public boolean isExistUsername(UserModifyDto modifyDto) {
        return userRepository.existsByUsername(modifyDto.username());
    }
}
