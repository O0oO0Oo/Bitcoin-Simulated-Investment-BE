package com.cryptocurrency.investment.user.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record UserAttendanceDto(
        List<LocalDate> date
) {
    static public UserAttendanceDto of(List<LocalDate> date) {
        return new UserAttendanceDto(date);
    }
}
