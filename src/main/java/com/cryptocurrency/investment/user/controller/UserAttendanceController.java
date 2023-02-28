package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.user.domain.Attendance;
import com.cryptocurrency.investment.user.dto.response.UserAttendanceDto;
import com.cryptocurrency.investment.user.service.UserAttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAttendanceController {
    private final UserAttendanceService attendanceService;

    @GetMapping("/attendance")
    public @ResponseBody ResponseWrapperDto attendanceList(Authentication authentication) {
        return ResponseWrapperDto.of(ResponseStatus.USER_ATTENDANCE_LIST_SUCCEED,
                UserAttendanceDto.of(
                        attendanceService.findAttendance(authentication)
                )
        );
    }

    @PostMapping("/attendance")
    public @ResponseBody ResponseWrapperDto attendanceAdd(Authentication authentication){
        List<LocalDate> attendance = attendanceService.addAttendance(authentication);

        if (attendance.isEmpty()) {
            return ResponseWrapperDto.of(ResponseStatus.USER_ATTENDANCE_ALREADY_DONE);
        }
        else {
            return ResponseWrapperDto.of(ResponseStatus.USER_ATTENDANCE_SUCCEED,
                    UserAttendanceDto.of(attendance)
            );
        }
    }
}
