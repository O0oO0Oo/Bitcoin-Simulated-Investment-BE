package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {
    List<UserAttendance> findByUserAccount_Id(UUID id);
    List<UserAttendance> findByUserAccount_IdAndDate(UUID id, LocalDate date);
}
