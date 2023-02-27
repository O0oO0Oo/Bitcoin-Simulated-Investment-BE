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
    @ManyToOne
    private UserAccount userAccount;
    @ManyToOne
    private Attendance attendance;
    private LocalDate date;
}
