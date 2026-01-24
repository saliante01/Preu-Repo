package com.backend.backendpreu.courses.controller;
import com.backend.backendpreu.courses.dto.CourseRequestDTO;
import com.backend.backendpreu.courses.model.Course;
import com.backend.backendpreu.courses.model.Subject;
import com.backend.backendpreu.courses.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller for managing base courses.
 * Provides endpoints for administrative operations on courses.
 */
@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Retrieves all available courses in the catalog.
     * Accessible only by users with 'ADMIN' role.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link Course} objects.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * Creates a new base course.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param request The {@link CourseRequestDTO} containing the details of the new course.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} containing the newly created {@link Course}.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> createCourse(
            @RequestBody CourseRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.createCourse(request, authentication.getName()));
    }

    /**
     * Updates an existing base course identified by its ID.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param id The ID of the course to update.
     * @param request The {@link CourseRequestDTO} containing the updated details.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} containing the updated {@link Course}.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, authentication.getName()));
    }

    /**
     * Deletes a base course identified by its ID.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param id The ID of the course to delete.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} with no content if the deletion is successful.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            Authentication authentication
    ) {
        courseService.deleteCourse(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of courses that currently have associated academic periods.
     * Accessible only by users with 'ADMIN' role.
     *
     * @return A {@link ResponseEntity} containing a list of {@link Course} objects with academic periods.
     */
    @GetMapping("/in-academic-periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> getCoursesInAcademicPeriods() {
        return ResponseEntity.ok(courseService.getCoursesWithAcademicPeriods());
    }

    /**
     * Retrieves a list of courses that do not currently have associated academic periods.
     * Accessible only by users with 'ADMIN' role.
     *
     * @return A {@link ResponseEntity} containing a list of {@link Course} objects without academic periods.
     */
    @GetMapping("/without-academic-periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> getCoursesWithoutAcademicPeriods() {
        return ResponseEntity.ok(courseService.getCoursesWithoutAcademicPeriods());
    }

    /**
     * Retrieves a list of all available subjects.
     * Accessible only by users with 'ADMIN' role.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link Subject} enum values.
     */
    @GetMapping("/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(Arrays.asList(Subject.values()));
    }
}
