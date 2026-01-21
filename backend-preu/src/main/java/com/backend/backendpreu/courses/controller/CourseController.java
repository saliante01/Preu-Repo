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

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // GET: Ver todo el catálogo
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // POST: Crear nuevo curso base
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> createCourse(
            @RequestBody CourseRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.createCourse(request, authentication.getName()));
    }

    // PUT: Actualizar curso base - NEW ENDPOINT
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, authentication.getName()));
    }

    // DELETE: Eliminar curso base - NEW ENDPOINT
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            Authentication authentication
    ) {
        courseService.deleteCourse(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // GET: Ver cursos que tienen periodos académicos asociados - NEW ENDPOINT
    @GetMapping("/in-academic-periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> getCoursesInAcademicPeriods() {
        return ResponseEntity.ok(courseService.getCoursesWithAcademicPeriods());
    }
    //GET: Ver cursos que no tiene periodos académicos asociados - NEW ENDPOINT
    @GetMapping("/without-academic-periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> getCoursesWithoutAcademicPeriods() {
        return ResponseEntity.ok(courseService.getCoursesWithoutAcademicPeriods());
    }
    //GET: Ver todos los subjects
    @GetMapping("/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(Arrays.asList(Subject.values()));
    }
}
