package com.cryptocurrency.investment.user.dto.response;

import com.cryptocurrency.investment.user.domain.UserAccount;

import java.time.LocalDate;

public record UserGetDto(
        String email,
        String username,
        LocalDate joinDate
) {
    static public UserGetDto of(UserAccount userAccount){
        return new UserGetDto(userAccount.getEmail(), userAccount.getUsername(), userAccount.getJoinDate());
    }
}
