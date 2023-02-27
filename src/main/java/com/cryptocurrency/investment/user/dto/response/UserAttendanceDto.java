package com.cryptocurrency.investment.user.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record UserAttendanceDto(
        List<LocalDate> localDate
) {
    static public UserAttendanceDto of(List<LocalDate> localDate) {
        return new UserAttendanceDto(localDate);
    }
}
