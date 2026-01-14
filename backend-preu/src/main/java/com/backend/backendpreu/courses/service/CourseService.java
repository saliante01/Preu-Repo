package com.backend.backendpreu.courses.service;
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

        // 2. Mappear DTO a Entidad
        Course newCourse = new Course();
        newCourse.setName(request.getName());
        newCourse.setCode(request.getCode());
        newCourse.setDescription(request.getDescription());
        // createdAt se llena automático por @PrePersist en tu entidad Course

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
}
