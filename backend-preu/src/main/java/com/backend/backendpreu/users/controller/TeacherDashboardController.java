package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.users.dto.TeacherDashboardDTO;
import com.backend.backendpreu.users.service.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}