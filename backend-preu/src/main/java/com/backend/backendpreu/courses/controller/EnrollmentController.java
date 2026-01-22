package com.backend.backendpreu.courses.controller;
import com.backend.backendpreu.courses.dto.EnrollmentRequestDTO;
import com.backend.backendpreu.courses.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔐 Solo Admins
    public ResponseEntity<String> enrollUser(
            @RequestBody EnrollmentRequestDTO request,
            Authentication authentication
    ) {
        // Pasamos el DTO y el email del admin autenticado
        enrollmentService.enrollUser(request, authentication.getName());

        return ResponseEntity.ok("Usuario inscrito exitosamente.");
    }
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unenrollUser(
            @RequestParam Long academicPeriodId,
            @RequestParam Long userId,
            Authentication authentication
    ) {
        enrollmentService.unenrollUser(academicPeriodId, userId, authentication.getName());
        return ResponseEntity.noContent().build(); // Retorna 204 (Éxito sin contenido)
    }
}
