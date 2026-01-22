package com.backend.backendpreu.courses.service;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.CourseRole;
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
        // 1. Buscar al Admin (Auditoría)
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        // 2. Buscar al Usuario a inscribir
        User userToEnroll = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 3. Buscar el Curso
        AcademicPeriod period = academicPeriodRepository.findById(request.getAcademicPeriodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo Académico no encontrado"));

        // 4. Validar DUPLICADOS
        boolean exists = participationRepository.existsByAcademicPeriodIdAndUserId(period.getId(), userToEnroll.getId());
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya está inscrito en este curso.");
        }

        // 5. 👇 VALIDACIÓN DE CUPOS Y SOBRECUPO (Lógica Nueva)
        Integer currentStudents = participationRepository.countByAcademicPeriodIdAndRole(
                period.getId(),
                CourseRole.STUDENT
        );

        // Verificamos si está lleno (o sobrepasado)
        if (currentStudents >= period.getMaxCapacity()) {
            // Si NO viene la orden de forzar, lanzamos error
            if (!request.isForceEnroll()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El curso está lleno (" + currentStudents + "/" + period.getMaxCapacity() + "). " +
                                "Se requiere autorización de sobrecupo (forceEnroll=true).");
            }
            // Si viene forceEnroll = true, el código continúa y permite la inscripción (Silla extra)
        }

        // 6. Crear Inscripción
        CourseParticipation participation = CourseParticipation.builder()
                .user(userToEnroll)
                .academicPeriod(period)
                .role(request.getRole())
                .status(ParticipationStatus.ACTIVE)
                .build();

        participationRepository.save(participation);

        // 7. Auditoría (Indicamos si fue forzado)
        String statusMsg = request.isForceEnroll() ? " (SOBRECUPO AUTORIZADO)" : "";

        String detailMessage = String.format("Inscripción creada%s: %s en curso ID %d (%d/%d)",
                statusMsg,
                userToEnroll.getEmail(),
                period.getId(),
                currentStudents + 1, // Nuevo total estimado
                period.getMaxCapacity());

        auditLogService.log(
                adminUser,
                "ENROLL_USER",
                "COURSE_PARTICIPATION",
                period.getId(),
                detailMessage
        );
    }
    @Transactional
    public void unenrollUser(Long academicPeriodId, Long userId, String adminEmail) {
        // 1. Verificar Admin (Para auditoría)
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        // 2. Buscar la inscripción específica (Validar que exista)
        // Nota: Asumimos que tu Repository tiene este método estándar. Si falla, avísame.
        CourseParticipation participation = participationRepository.findByAcademicPeriodIdAndUserId(academicPeriodId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La inscripción no existe para este usuario en este periodo."));

        // 3. Eliminar
        participationRepository.delete(participation);

        // 4. Auditoría
        auditLogService.log(
                adminUser,
                "UNENROLL_USER",
                "COURSE_PARTICIPATION",
                participation.getId(),
                "Eliminó inscripción de: " + participation.getUser().getEmail() + " (Rol: " + participation.getRole() + ")"
        );
}
}