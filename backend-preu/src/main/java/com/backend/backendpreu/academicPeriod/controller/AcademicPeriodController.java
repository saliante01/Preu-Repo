package com.backend.backendpreu.academicPeriod.controller;

import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodCreateDTO;
import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodSummaryDTO;
import com.backend.backendpreu.academicPeriod.dto.TeacherAssignmentDTO;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.service.AcademicPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-periods")
@RequiredArgsConstructor
public class AcademicPeriodController {

    private final AcademicPeriodService academicPeriodService;

    @GetMapping("/available-for/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getAvailableForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicPeriodService.getAvailablePeriodsForStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriod> createPeriod(@RequestBody AcademicPeriodCreateDTO request, Authentication authentication) {
        return ResponseEntity.ok(academicPeriodService.createPeriod(request, authentication.getName()));
    }

    @PatchMapping("/{id}/request-closure")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> requestClosure(@PathVariable Long id, Authentication authentication) {
        academicPeriodService.requestClosure(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/approve-closure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveClosure(@PathVariable Long id, Authentication authentication) {
        academicPeriodService.approveClosure(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getByTerm(@PathVariable Long termId) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByTerm(termId));
    }

    @GetMapping("/pending-closures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getPendingClosures() {
        return ResponseEntity.ok(academicPeriodService.getPendingClosures());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getAllPeriods() {
        return ResponseEntity.ok(academicPeriodService.getAllPeriods());
    }
    // Asignar Profesor
    @PostMapping("/{periodId}/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignTeacher(
            @PathVariable Long periodId,
            @RequestBody TeacherAssignmentDTO request, // Crea un DTO simple con { "teacherId": 2 }
            Authentication authentication
    ) {
        academicPeriodService.assignTeacherToPeriod(periodId, request.getTeacherId(), authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESSOR')")
    public ResponseEntity<AcademicPeriodSummaryDTO> getOnePeriod(@PathVariable Long id) {
        return ResponseEntity.ok(academicPeriodService.getPeriodById(id));
    }

    // NUEVO ENDPOINT: Obtener períodos académicos por ID de curso
    @GetMapping("/by-course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')") // Admins y Profesores pueden ver los horarios de un curso
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getPeriodsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByCourse(courseId));
    }

    // NUEVO ENDPOINT: Obtener participantes de un período académico
    @GetMapping("/{periodId}/participants")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')") // Admins y Profesores pueden ver los participantes
    public ResponseEntity<List<com.backend.backendpreu.courses.dto.CourseParticipantDTO>> getPeriodParticipants(@PathVariable Long periodId) {
        return ResponseEntity.ok(academicPeriodService.getParticipants(periodId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriod> updatePeriod(
            @PathVariable Long id,
            @RequestBody AcademicPeriodCreateDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(academicPeriodService.updatePeriod(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id, Authentication authentication) {
        // Llamamos al servicio pasando el ID y el email del admin (para auditoría)
        academicPeriodService.deletePeriod(id, authentication.getName());

        // Retornamos 204 No Content (Estándar para borrados exitosos)
        return ResponseEntity.noContent().build();
    }
}
