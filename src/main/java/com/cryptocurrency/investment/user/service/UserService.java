package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.dto.request.UserModifyDto;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    public Optional<UserAccount> findUser(String token) {
        UUID id = UUID.fromString(jwtUtils.getSubject(token));
        return userRepository.findById(id);
    }

    public int modifyUser(String token, UserModifyDto modifyDto) {
        UUID id = UUID.fromString(jwtUtils.getSubject(token));
        return userRepository.updateUser(id, modifyDto.username(), modifyDto.password());
    }

    public int deleteUser(String token) {
        UUID id = UUID.fromString(jwtUtils.getSubject(token));
        return userRepository.deleteById(id);
    }

    public boolean findUserByUsername(UserModifyDto modifyDto) {
        return userRepository.existsByUsername(modifyDto.username());
    }

    public Optional<UserAccount> findUserById(Authentication authentication) {
        UUID id = UUID.fromString((authentication.getName()));
        return userRepository.findById(id);
    }
}
