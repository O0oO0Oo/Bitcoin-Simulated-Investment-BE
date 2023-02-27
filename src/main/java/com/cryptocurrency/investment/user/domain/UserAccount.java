package com.cryptocurrency.investment.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate joinDate;
    @OneToMany(mappedBy = "userAccount")
    private List<UserAttendance> attendances = new ArrayList<>();
    private boolean isDeleted;
    private boolean isSuspended;
    private LocalDate suspensionDate;
}