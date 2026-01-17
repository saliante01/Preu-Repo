package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.users.dto.AvailableTeacherDTO;
import com.backend.backendpreu.users.dto.TeacherDashboardDTO;
import com.backend.backendpreu.users.service.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherDashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getTeachersDashboard());
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AvailableTeacherDTO>> getAvailable(
            @RequestParam String subject,
            @RequestParam(defaultValue = "40") int maxHours
    ) {
        return ResponseEntity.ok(dashboardService.findAvailableTeachers(subject, maxHours));
    }
    @GetMapping("/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherDashboardDTO> getTeacherDetails(@PathVariable Long teacherId) {
        return ResponseEntity.ok(dashboardService.getTeacherStats(teacherId));
    }
}