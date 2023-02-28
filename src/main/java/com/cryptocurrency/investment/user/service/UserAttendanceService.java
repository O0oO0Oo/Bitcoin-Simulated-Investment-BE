package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.Attendance;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.domain.UserAttendance;
import com.cryptocurrency.investment.user.repository.AttendanceRepository;
import com.cryptocurrency.investment.user.repository.UserAttendanceRepository;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAttendanceService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserAttendanceRepository userAttendanceRepository;

    public List<LocalDate> findAttendance(Authentication authentication) {
        UUID id = UUID.fromString(authentication.getName());
        return userAttendanceRepository.findByUserAccount_Id(id).stream().map(
                UserAttendance::getDate
        ).collect(Collectors.toList());
    }

    public List<LocalDate> addAttendance(Authentication authentication){
        UserAttendance userAttendance = new UserAttendance();
        UUID id = UUID.fromString(authentication.getName());

        Optional<UserAccount> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }
        UserAccount user = userOpt.get();

        Optional<Attendance> attendanceOpt = attendanceRepository.findByDate(LocalDate.now());
        if (attendanceOpt.isEmpty()) {
            Attendance attendance = new Attendance();
            attendanceRepository.save(attendance);

            userAttendance.setAttendance(attendance);
            userAttendance.setUserAccount(user);
            userAttendance.setDate(attendance.getDate());
        } else {
            userAttendance.setAttendance(attendanceOpt.get());
            userAttendance.setUserAccount(user);
            userAttendance.setDate(attendanceOpt.get().getDate());
        }

        if (userAttendanceRepository.findByUserAccount_IdAndDate(
                user.getId(),
                userAttendance.getDate()
        ).isEmpty()
        ) {
            userAttendanceRepository.save(userAttendance);
        } else {
            return Collections.emptyList();
        }

        return userAttendanceRepository.findByUserAccount_Id(user.getId())
                .stream()
                .map(
                        UserAttendance::getDate
                ).collect(Collectors.toList());
    }
}
