package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {
    List<UserAttendance> findByUserAccountId(Long id);
    List<UserAttendance> findByUserAccount_IdAndDate(Long id, LocalDate date);
}
