package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    public Optional<UserAccount> userInfo(String token) {
        String email = jwtUtils.getSubject(token);
        return userRepository.findByEmail(email);
    }

    public HashSet<LocalDate> getUserAttendance(String token){
        String email = jwtUtils.getSubject(token);
        return userRepository.findByEmail(email).get().getAttendance();
    }

    public HashSet<LocalDate> userAttendance(String token){
        String email = jwtUtils.getSubject(token);
        UserAccount userAccount = userRepository.findByEmail(email).get();
        userAccount.getAttendance().add(LocalDate.now());
        userRepository.save(userAccount);
        return userAccount.getAttendance();
    }
}
