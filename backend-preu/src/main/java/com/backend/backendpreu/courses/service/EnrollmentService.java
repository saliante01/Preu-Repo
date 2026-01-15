package com.backend.backendpreu.courses.service;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import com.backend.backendpreu.academicPaticipation.repository.CourseParticipationRepository;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.repository.AcademicPeriodRepository;
import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.courses.dto.EnrollmentRequestDTO;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void enrollUser(EnrollmentRequestDTO request, String adminEmail) {
        // 1. Buscar al Admin que ejecuta la acción (Para la auditoría)
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        // 2. Buscar al Usuario a inscribir
        User userToEnroll = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + request.getUserId()));

        // 3. Buscar el Curso (Periodo Académico)
        AcademicPeriod period = academicPeriodRepository.findById(request.getAcademicPeriodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo Académico no encontrado con ID: " + request.getAcademicPeriodId()));

        // 4. Validar DUPLICADOS (Regla de Negocio Crítica)
        boolean exists = participationRepository.existsByAcademicPeriodIdAndUserId(period.getId(), userToEnroll.getId());
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya está inscrito en este curso.");
        }


        CourseParticipation participation = CourseParticipation.builder()
                .user(userToEnroll)
                .academicPeriod(period)
                .role(request.getRole())
                .status(ParticipationStatus.ACTIVE)
                // enrolledAt se llena automático por @PrePersist en la entidad
                .build();

        participationRepository.save(participation);


        String detailMessage = String.format("Inscripción creada: %s (%s) en curso ID %d como %s",
                userToEnroll.getEmail(), userToEnroll.getFirstName(), period.getId(), request.getRole());

        auditLogService.log(
                adminUser,              // Actor
                "ENROLL_USER",          // Acción
                "COURSE_PARTICIPATION", // Entidad Afectada
                period.getId(),         // ID Entidad (Usamos el del curso como referencia)
                detailMessage           // Detalle
        );
    }
}