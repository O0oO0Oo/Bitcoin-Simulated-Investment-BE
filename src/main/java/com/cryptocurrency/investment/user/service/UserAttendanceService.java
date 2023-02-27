package com.cryptocurrency.investment.user.service;

import com.cryptocurrency.investment.user.domain.Attendance;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.domain.UserAttendance;
import com.cryptocurrency.investment.user.repository.AttendanceRepository;
import com.cryptocurrency.investment.user.repository.UserAttendanceRepository;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAttendanceService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserAttendanceRepository userAttendanceRepository;

    public List<LocalDate> findAttendance(String email) {

        return userRepository.findByEmail(email).map(
                user -> userAttendanceRepository.findByUserAccountId(user.getId())
                        .stream()
                        .map(
                                UserAttendance::getDate
                ).collect(Collectors.toList())
        ).orElse(
                Collections.emptyList()
        );
    }

    public List<LocalDate> addAttendance(String email){
        UserAttendance userAttendance = new UserAttendance();
        Optional<UserAccount> userOpt = userRepository.findByEmail(email);
        Optional<Attendance> attendanceOpt = attendanceRepository.findByDate(LocalDate.now());

        if (attendanceOpt.isEmpty() && userOpt.isPresent()) {
            Attendance attendance = new Attendance();
            attendance.setDate(LocalDate.now());
            attendanceRepository.save(attendance);

            userAttendance.setAttendance(attendance);
            userAttendance.setUserAccount(userOpt.get());
            userAttendance.setDate(attendance.getDate());
        } else {
            userAttendance.setAttendance(attendanceOpt.get());
            userAttendance.setUserAccount(userOpt.get());
            userAttendance.setDate(attendanceOpt.get().getDate());
        }

        if(userAttendanceRepository.findByUserAccount_IdAndDate(
                    userOpt.get().getId(),
                    attendanceOpt.get().getDate()
                ).isEmpty()
        ) {
            userAttendanceRepository.save(userAttendance);
        }
        else{
            return Collections.emptyList();
        }

        return userAttendanceRepository.findByUserAccountId(userOpt.get().getId())
                .stream()
                .map(
                        UserAttendance::getDate
                ).collect(Collectors.toList());
    }
}
