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

/**
 * REST controller for managing academic periods.
 * Provides endpoints for administrators and authenticated users to
 * view, create, update, and manage the lifecycle of academic periods.
 */
@RestController
@RequestMapping("/api/academic-periods")
@RequiredArgsConstructor
public class AcademicPeriodController {

    private final AcademicPeriodService academicPeriodService;

    /**
     * Retrieves a list of academic periods available for a specific student.
     * This typically includes periods the student is not yet enrolled in.
     *
     * @param studentId The ID of the student.
     * @return A {@link ResponseEntity} containing a list of {@link AcademicPeriodSummaryDTO}.
     */
    @GetMapping("/available-for/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getAvailableForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicPeriodService.getAvailablePeriodsForStudent(studentId));
    }

    /**
     * Creates a new academic period.
     * Only accessible by administrators.
     *
     * @param request The DTO containing data for the new academic period.
     * @param authentication The authentication object of the logged-in admin.
     * @return A {@link ResponseEntity} containing the created {@link AcademicPeriod}.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriod> createPeriod(@RequestBody AcademicPeriodCreateDTO request, Authentication authentication) {
        return ResponseEntity.ok(academicPeriodService.createPeriod(request, authentication.getName()));
    }

    /**
     * Requests the closure of an academic period.
     * Accessible by administrators and teachers.
     *
     * @param id The ID of the academic period to close.
     * @param authentication The authentication object of the logged-in user.
     * @return A {@link ResponseEntity} with no content on successful request.
     */
    @PatchMapping("/{id}/request-closure")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> requestClosure(@PathVariable Long id, Authentication authentication) {
        academicPeriodService.requestClosure(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Approves the closure of an academic period.
     * Only accessible by administrators.
     *
     * @param id The ID of the academic period to approve closure for.
     * @param authentication The authentication object of the logged-in admin.
     * @return A {@link ResponseEntity} with no content on successful approval.
     */
    @PatchMapping("/{id}/approve-closure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveClosure(@PathVariable Long id, Authentication authentication) {
        academicPeriodService.approveClosure(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves academic periods filtered by school term ID.
     * Accessible by administrators and teachers.
     *
     * @param termId The ID of the school term.
     * @return A {@link ResponseEntity} containing a list of {@link AcademicPeriodSummaryDTO}.
     */
    @GetMapping("/by-term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getByTerm(@PathVariable Long termId) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByTerm(termId));
    }

    /**
     * Retrieves a list of academic periods that are pending closure.
     * Only accessible by administrators.
     *
     * @return A {@link ResponseEntity} containing a list of {@link AcademicPeriodSummaryDTO}.
     */
    @GetMapping("/pending-closures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getPendingClosures() {
        return ResponseEntity.ok(academicPeriodService.getPendingClosures());
    }

    /**
     * Retrieves all academic periods.
     * Only accessible by administrators.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link AcademicPeriodSummaryDTO}.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getAllPeriods() {
        return ResponseEntity.ok(academicPeriodService.getAllPeriods());
    }

    /**
     * Assigns a teacher to a specific academic period.
     * Only accessible by administrators.
     *
     * @param periodId The ID of the academic period.
     * @param request A DTO containing the teacher's ID.
     * @param authentication The authentication object of the logged-in admin.
     * @return A {@link ResponseEntity} with no content on successful assignment.
     */
    @PostMapping("/{periodId}/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignTeacher(
            @PathVariable Long periodId,
            @RequestBody TeacherAssignmentDTO request,
            Authentication authentication
    ) {
        academicPeriodService.assignTeacherToPeriod(periodId, request.getTeacherId(), authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves details for a single academic period by its ID.
     * Accessible by administrators, students, and professors.
     *
     * @param id The ID of the academic period.
     * @return A {@link ResponseEntity} containing the {@link AcademicPeriodSummaryDTO}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESSOR')")
    public ResponseEntity<AcademicPeriodSummaryDTO> getOnePeriod(@PathVariable Long id) {
        return ResponseEntity.ok(academicPeriodService.getPeriodById(id));
    }

    /**
     * Retrieves all academic periods associated with a specific course.
     * Accessible by administrators and professors.
     *
     * @param courseId The ID of the course.
     * @return A {@link ResponseEntity} containing a list of {@link AcademicPeriodSummaryDTO}.
     */
    @GetMapping("/by-course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<AcademicPeriodSummaryDTO>> getPeriodsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByCourse(courseId));
    }

    /**
     * Retrieves the participants (students and professors) of a specific academic period.
     * Accessible by administrators and professors.
     *
     * @param periodId The ID of the academic period.
     * @return A {@link ResponseEntity} containing a list of {@link com.backend.backendpreu.courses.dto.CourseParticipantDTO}.
     */
    @GetMapping("/{periodId}/participants")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<com.backend.backendpreu.courses.dto.CourseParticipantDTO>> getPeriodParticipants(@PathVariable Long periodId) {
        return ResponseEntity.ok(academicPeriodService.getParticipants(periodId));
    }

    /**
     * Deletes an academic period by its ID.
     * Only accessible by administrators.
     *
     * @param id The ID of the academic period to delete.
     * @param authentication The authentication object of the logged-in admin.
     * @return A {@link ResponseEntity} with no content on successful deletion.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id, Authentication authentication) {
        academicPeriodService.deletePeriod(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
