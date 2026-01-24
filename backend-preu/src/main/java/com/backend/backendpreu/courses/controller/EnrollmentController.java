package com.backend.backendpreu.courses.controller;
import com.backend.backendpreu.courses.dto.EnrollmentRequestDTO;
import com.backend.backendpreu.courses.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user enrollments in academic periods.
 * Provides endpoints for administrative enrollment and unenrollment operations.
 */
@RestController
@RequestMapping("/api/admin/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Enrolls a user in a specific academic period.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param request The {@link EnrollmentRequestDTO} containing the enrollment details.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔐 Solo Admins
    public ResponseEntity<String> enrollUser(
            @RequestBody EnrollmentRequestDTO request,
            Authentication authentication
    ) {
        // Pass the DTO and the authenticated admin's email
        enrollmentService.enrollUser(request, authentication.getName());

        return ResponseEntity.ok("User enrolled successfully.");
    }

    /**
     * Unenrolls a user from a specific academic period.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param academicPeriodId The ID of the academic period from which to unenroll the user.
     * @param userId The ID of the user to unenroll.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} with no content (204) if the unenrollment is successful.
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unenrollUser(
            @RequestParam Long academicPeriodId,
            @RequestParam Long userId,
            Authentication authentication
    ) {
        enrollmentService.unenrollUser(academicPeriodId, userId, authentication.getName());
        return ResponseEntity.noContent().build(); // Returns 204 (Success with no content)
    }
}
