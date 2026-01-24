package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.courses.model.Subject;
import com.backend.backendpreu.users.dto.AvailableTeacherDTO;
import com.backend.backendpreu.users.dto.TeacherDashboardDTO;
import com.backend.backendpreu.users.service.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador administrativo para visualización de estadísticas de profesores.
 *
 * Permite:
 * - Ver dashboard general de profesores
 * - Ver profesores disponibles por ramo
 * - Ver detalle de un profesor específico
 *
 * Seguridad:
 * - Solo accesible por ADMIN
 *
 * Ruta base:
 * /api/teachers
 */
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherDashboardController {

    /**
     * Servicio de estadísticas y dashboard de profesores
     */
    private final TeacherDashboardService dashboardService;

    /**
     * Obtiene dashboard completo de todos los profesores.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherDashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getTeachersDashboard());
    }

    /**
     * Busca profesores disponibles para un ramo específico.
     *
     * @param subject Ramo requerido
     * @param maxHours Máximo de horas permitidas
     */
    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AvailableTeacherDTO>> getAvailable(
            @RequestParam Subject subject,
            @RequestParam(defaultValue = "40") int maxHours
    ) {
        return ResponseEntity.ok(dashboardService.findAvailableTeachers(subject, maxHours));
    }

    /**
     * Obtiene estadísticas detalladas de un profesor específico.
     */
    @GetMapping("/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherDashboardDTO> getTeacherDetails(@PathVariable Long teacherId) {
        return ResponseEntity.ok(dashboardService.getTeacherStats(teacherId));
    }
}
