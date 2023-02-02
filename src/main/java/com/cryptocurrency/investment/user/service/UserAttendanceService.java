package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserAttendanceService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    public HashSet<LocalDate> getAttendanceList(String token){
        String email = jwtUtils.getSubject(token);
        return userRepository.findByEmail(email).get().getAttendance();
    }

    public HashSet<LocalDate> doAttendance(String token){
        String email = jwtUtils.getSubject(token);
        UserAccount userAccount = userRepository.findByEmail(email).get();
        userAccount.getAttendance().add(LocalDate.now());
        userRepository.save(userAccount);
        return userAccount.getAttendance();
    }
}
