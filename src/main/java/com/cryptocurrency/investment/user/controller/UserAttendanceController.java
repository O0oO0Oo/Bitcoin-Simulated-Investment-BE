package com.cryptocurrency.investment.user.controller;

import com.cryptocurrency.investment.user.service.UserAttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAttendanceController {
    private final UserAttendanceService attendanceService;

    @GetMapping("/attendance")
    public @ResponseBody HashSet<LocalDate> getAttendance(HttpServletRequest request) {
        return attendanceService.getAttendanceList(request.getHeader("Authorization").replace("Bearer ", ""));
    }

    @PostMapping("/attendance")
    public @ResponseBody HashSet<LocalDate> attendance(HttpServletRequest request){
        return attendanceService.doAttendance(request.getHeader("Authorization").replace("Bearer ", ""));
    }
}
