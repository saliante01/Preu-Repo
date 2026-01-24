package com.backend.backendpreu.courses.service;
import com.backend.backendpreu.academicPeriod.repository.AcademicPeriodRepository;
import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.courses.dto.CourseRequestDTO;
import com.backend.backendpreu.courses.model.Course;
import com.backend.backendpreu.courses.Repository.CourseRepository;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing base courses.
 * Provides business logic for CRUD operations on Course entities,
 * including validations and audit logging.
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final AcademicPeriodRepository academicPeriodRepository;

    /**
     * Retrieves all available courses.
     *
     * @return A list of all {@link Course} objects.
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Creates a new course based on the provided request DTO.
     * Performs validations for unique code and name, and logs the action.
     *
     * @param request The {@link CourseRequestDTO} containing the course details.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @return The newly created {@link Course} object.
     * @throws ResponseStatusException if a course with the same code or name already exists, or if the admin is not found.
     */
    @Transactional
    public Course createCourse(CourseRequestDTO request, String adminEmail) {

        // 1. Validate Unique Code (Business Rule)
        if (courseRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A course with code: " + request.getCode() + " already exists.");
        }
        // 1. Validate Unique Name (Business Rule)
        if (courseRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A course with name: " + request.getName() + " already exists.");
        }

        // 2. Map DTO to Entity
        Course newCourse = new Course();
        newCourse.setName(request.getName());
        newCourse.setCode(request.getCode());
        newCourse.setDescription(request.getDescription());
        newCourse.setSubject(request.getSubject());
        Course savedCourse = courseRepository.save(newCourse);

        // 3. Log Audit
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        auditLogService.log(
                admin,
                "CREATE_COURSE",
                "COURSE",
                savedCourse.getId(),
                "Created base course: " + savedCourse.getName() + " (" + savedCourse.getCode() + ")"
        );

        return savedCourse;
    }

    /**
     * Retrieves a list of courses that have associated academic periods.
     *
     * @return A list of {@link Course} objects with academic periods.
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesWithAcademicPeriods() {
        return courseRepository.findAll().stream()
                .filter(course -> academicPeriodRepository.existsByCourseId(course.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of courses that do not have associated academic periods.
     *
     * @return A list of {@link Course} objects without academic periods.
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesWithoutAcademicPeriods() {
        return courseRepository.findAll().stream()
                .filter(course -> !academicPeriodRepository.existsByCourseId(course.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing course identified by its ID.
     * Performs validations for unique code and name (if changed) and logs the action.
     *
     * @param id The ID of the course to update.
     * @param request The {@link CourseRequestDTO} containing the updated course details.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @return The updated {@link Course} object.
     * @throws ResponseStatusException if the course is not found, or if a course with the updated code or name already exists.
     */
    @Transactional
    public Course updateCourse(Long id, CourseRequestDTO request, String adminEmail) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + id));

        // Validate unique code (if it changes and already exists in another course)
        if (!existingCourse.getCode().equals(request.getCode()) && courseRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another course with code: " + request.getCode() + " already exists.");
        }

        // Validate unique name (if it changes and already exists in another course)
        if (!existingCourse.getName().equals(request.getName()) && courseRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another course with name: " + request.getName() + " already exists.");
        }

        existingCourse.setName(request.getName());
        existingCourse.setCode(request.getCode());
        existingCourse.setDescription(request.getDescription());
        existingCourse.setSubject(request.getSubject());
        Course updatedCourse = courseRepository.save(existingCourse);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        auditLogService.log(
                admin,
                "UPDATE_COURSE",
                "COURSE",
                updatedCourse.getId(),
                "Updated base course: " + updatedCourse.getName() + " (" + updatedCourse.getCode() + ")"
        );

        return updatedCourse;
    }

    /**
     * Deletes a course identified by its ID.
     * Prevents deletion if the course has associated academic periods and logs the action.
     *
     * @param id The ID of the course to delete.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @throws ResponseStatusException if the course is not found, or if it has associated academic periods.
     */
    @Transactional
    public void deleteCourse(Long id, String adminEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + id));

        // Validate if the course is associated with any AcademicPeriod
        if (academicPeriodRepository.existsByCourseId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete course '" + course.getName() + "' because it has associated academic periods. Delete them first.");
        }

        courseRepository.delete(course);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        auditLogService.log(
                admin,
                "DELETE_COURSE",
                "COURSE",
                id,
                "Deleted base course: " + course.getName() + " (" + course.getCode() + ")"
        );
    }

}
