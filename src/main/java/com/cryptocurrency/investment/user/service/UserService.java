package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.dto.request.UserModifyDto;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Optional<UserAccount> findUser(Authentication authentication) {
        UUID id = UUID.fromString(authentication.getName());
        return userRepository.findById(id);
    }

    public int modifyUser(Authentication authentication, UserModifyDto modifyDto) {
        UUID id = UUID.fromString(authentication.getName());
        String username = modifyDto.username();
        String password = passwordEncoder.encode(modifyDto.password());
        return userRepository.updateUser(id, username, password);
    }

    /**
     * Soft Delete Hard Delete 구현
     * @param authentication
     * @return
     */
    public int deleteUser(Authentication authentication) {
        UUID id = UUID.fromString(authentication.getName());
        return userRepository.deleteById(id);
    }

    public boolean findUserByUsername(UserModifyDto modifyDto) {
        String username = modifyDto.username();
        return userRepository.existsByUsername(username);
    }

    public Optional<UserAccount> findUserById(Authentication authentication) {
        UUID id = UUID.fromString((authentication.getName()));
        return userRepository.findById(id);
    }
}
