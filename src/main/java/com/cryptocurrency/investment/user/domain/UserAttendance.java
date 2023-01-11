package com.cryptocurrency.investment.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class UserAttendance {
    @Id
    @GeneratedValue
    private String Id;

    private LocalDateTime date;
}
