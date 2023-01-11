package com.cryptocurrency.investment.user.domain;

import com.cryptocurrency.investment.user.validation.SaveCheck;
import com.cryptocurrency.investment.user.validation.UpdateCheck;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@Entity
public class UserAccount {
    @Id
    @Column(length = 50)
    private String userId;

    @NotNull(groups = {SaveCheck.class})
    @NotBlank(groups = {UpdateCheck.class})
    private String userPassword;

    @NotNull
    @NotEmpty
    @NotBlank
    private String nickName;


    @NotNull
    @NotEmpty
    @NotBlank
    private String email;


    private LocalDateTime joinDate;

    private Role role;
}
