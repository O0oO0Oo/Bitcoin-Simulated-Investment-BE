package com.cryptocurrency.investment.user.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
public class UserAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount userAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Attendance attendance;
    private LocalDate date;
}
