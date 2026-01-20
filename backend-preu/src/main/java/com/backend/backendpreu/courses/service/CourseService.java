package com.backend.backendpreu.courses.service;
import com.backend.backendpreu.academicPeriod.repository.AcademicPeriodRepository; // NEW IMPORT
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

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final AcademicPeriodRepository academicPeriodRepository; // NEW DEPENDENCY

    // Listar Catálogo
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Crear Nuevo Curso
    @Transactional
    public Course createCourse(CourseRequestDTO request, String adminEmail) {

        // 1. Validar Código Único (Regla de Negocio)
        if (courseRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un curso con el código: " + request.getCode());
        }
        // 1. Validar Nombre Único (Regla de Negocio) - ADDED
        if (courseRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un curso con el nombre: " + request.getName());
        }

        // 2. Mappear DTO a Entidad
        Course newCourse = new Course();
        newCourse.setName(request.getName());
        newCourse.setCode(request.getCode());
        newCourse.setDescription(request.getDescription());

        Course savedCourse = courseRepository.save(newCourse);

        // 3. Registrar Auditoría
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        auditLogService.log(
                admin,
                "CREATE_COURSE",
                "COURSE",
                savedCourse.getId(),
                "Creó curso base: " + savedCourse.getName() + " (" + savedCourse.getCode() + ")"
        );

        return savedCourse;
    }

    // Obtener Cursos con Períodos Académicos Asociados - NEW METHOD
    @Transactional(readOnly = true)
    public List<Course> getCoursesWithAcademicPeriods() {
        return courseRepository.findAll().stream()
                .filter(course -> academicPeriodRepository.existsByCourseId(course.getId()))
                .collect(Collectors.toList());
    }

    // Actualizar Curso
    @Transactional
    public Course updateCourse(Long id, CourseRequestDTO request, String adminEmail) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado con ID: " + id));

        // Validar código único (si cambia y ya existe en otro curso)
        if (!existingCourse.getCode().equals(request.getCode()) && courseRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro curso con el código: " + request.getCode());
        }

        // Validar nombre único (si cambia y ya existe en otro curso)
        if (!existingCourse.getName().equals(request.getName()) && courseRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro curso con el nombre: " + request.getName());
        }

        existingCourse.setName(request.getName());
        existingCourse.setCode(request.getCode());
        existingCourse.setDescription(request.getDescription());

        Course updatedCourse = courseRepository.save(existingCourse);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        auditLogService.log(
                admin,
                "UPDATE_COURSE",
                "COURSE",
                updatedCourse.getId(),
                "Actualizó curso base: " + updatedCourse.getName() + " (" + updatedCourse.getCode() + ")"
        );

        return updatedCourse;
    }

    // Eliminar Curso
    @Transactional
    public void deleteCourse(Long id, String adminEmail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado con ID: " + id));

        // Validar si el curso está asociado a algún AcademicPeriod
        if (academicPeriodRepository.existsByCourseId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el curso '" + course.getName() + "' porque tiene períodos académicos asociados. Elimínelos primero.");
        }

        courseRepository.delete(course);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        auditLogService.log(
                admin,
                "DELETE_COURSE",
                "COURSE",
                id,
                "Eliminó curso base: " + course.getName() + " (" + course.getCode() + ")"
        );
    }
}
