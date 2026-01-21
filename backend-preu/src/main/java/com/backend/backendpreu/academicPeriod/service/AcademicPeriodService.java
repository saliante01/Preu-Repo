package com.backend.backendpreu.academicPeriod.service;

import com.backend.backendpreu.courses.dto.CourseParticipantDTO; // Asegúrate de tener este DTO creado
import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import com.backend.backendpreu.academicPaticipation.repository.CourseParticipationRepository;
import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodCreateDTO;
import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodSummaryDTO;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriodStatus;
import com.backend.backendpreu.academicPeriod.model.ClassSchedule;
import com.backend.backendpreu.academicPeriod.repository.AcademicPeriodRepository;
import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.courses.Repository.CourseRepository;
import com.backend.backendpreu.courses.Repository.SchoolTermRepository;
import com.backend.backendpreu.courses.model.Course;
import com.backend.backendpreu.courses.model.SchoolTerm;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicPeriodService {

    private final AcademicPeriodRepository academicPeriodRepository;
    private final CourseRepository courseRepository;
    private final SchoolTermRepository schoolTermRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final CourseParticipationRepository participationRepository;

    // --- LECTURA ---
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getAvailablePeriodsForStudent(Long studentId) {
        List<AcademicPeriod> periods = academicPeriodRepository.findAvailableForStudent(studentId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AcademicPeriodSummaryDTO getPeriodById(Long id) {
        AcademicPeriod period = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));
        return mapToDTO(period);
    }

    // --- LECTURA DE PARTICIPANTES (Paso 12 del Flujo) ---
    @Transactional(readOnly = true)
    public List<CourseParticipantDTO> getParticipants(Long periodId) {
        List<CourseParticipation> participations = participationRepository
                .findByAcademicPeriodIdAndStatus(periodId, ParticipationStatus.ACTIVE);

        return participations.stream().map(p -> CourseParticipantDTO.builder()
                        .userId(p.getUser().getId())
                        .fullName(p.getUser().getFirstName() + " " + p.getUser().getLastName())
                        .email(p.getUser().getEmail())
                        .role(p.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    // --- CREACIÓN ---
    @Transactional
    public AcademicPeriod createPeriod(AcademicPeriodCreateDTO request, String adminEmail) {
        // 1. Validar Curso
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        // 2. Validar Semestre
        SchoolTerm term = schoolTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semestre no encontrado"));

        // 3. Validar Fechas
        if (request.getStartDate().isBefore(term.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha inicio curso anterior al inicio del semestre");
        }
        if (request.getEndDate().isAfter(term.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha fin curso posterior al fin del semestre");
        }

        AcademicPeriod period = new AcademicPeriod();
        period.setCourse(course);
        period.setSchoolTerm(term);
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setStatus(AcademicPeriodStatus.ACTIVE);
        period.setMaxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : 30);

        // 4. Validar Duplicados de Horario (si se proporciona un horario)
        if (request.getDayOfWeek() != null && request.getStartTime() != null && request.getEndTime() != null) {
            boolean existsDuplicateSchedule = academicPeriodRepository.existsByCourseIdAndSchoolTermIdAndSchedule_DayOfWeekAndSchedule_StartTimeAndSchedule_EndTime(
                    request.getCourseId(),
                    request.getTermId(),
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime()
            );

            if (existsDuplicateSchedule) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un período académico con la misma asignatura, semestre, día y horario.");
            }
        }

        // 5. Crear Horario (ahora es el paso 5)
        if (request.getDayOfWeek() != null && request.getStartTime() != null && request.getEndTime() != null) {
            ClassSchedule schedule = ClassSchedule.builder()
                    .dayOfWeek(request.getDayOfWeek())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .build();
            period.setSchedule(schedule);
        }

        AcademicPeriod savedPeriod = academicPeriodRepository.save(period);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "OPEN_PERIOD", "ACADEMIC_PERIOD", savedPeriod.getId(),
                "Abrió curso " + course.getCode() + " (Cupo: " + period.getMaxCapacity() + ")");

        return savedPeriod;
    }

    // --- ACTUALIZACIÓN (Paso 10 del Flujo) ---
    @Transactional
    public AcademicPeriod updatePeriod(Long periodId, AcademicPeriodCreateDTO request, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        // Actualizar datos básicos
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        if(request.getMaxCapacity() != null) {
            period.setMaxCapacity(request.getMaxCapacity());
        }

        // Actualizar Horario
        if (request.getDayOfWeek() != null) {
            if (period.getSchedule() == null) {
                period.setSchedule(new ClassSchedule());
            }
            period.getSchedule().setDayOfWeek(request.getDayOfWeek());
            period.getSchedule().setStartTime(request.getStartTime());
            period.getSchedule().setEndTime(request.getEndTime());
        }

        // Auditoría
        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "UPDATE_PERIOD", "ACADEMIC_PERIOD", periodId, "Actualizó horario/fechas");

        return academicPeriodRepository.save(period);
    }

    // --- ELIMINAR (Paso 10 del Flujo) ---
    @Transactional
    public void deletePeriod(Long periodId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        // Seguridad: No borrar si hay participantes (alumnos o profesores)
        Integer enrollmentCount = participationRepository.countByAcademicPeriodIdAndStatus(
                periodId,
                ParticipationStatus.ACTIVE
        );

        if (enrollmentCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: Hay " + enrollmentCount + " participantes activos. Dales de baja primero.");
        }

        academicPeriodRepository.delete(period);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "DELETE_PERIOD", "ACADEMIC_PERIOD", periodId,
                "Eliminó el horario del curso ID: " + periodId);
    }

    // --- ASIGNAR PROFESOR ---
    @Transactional
    public void assignTeacherToPeriod(Long periodId, Long teacherId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado"));

        boolean alreadyAssigned = participationRepository.existsByAcademicPeriodIdAndUserId(periodId, teacherId);
        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este profesor ya está asignado.");
        }

        CourseParticipation participation = CourseParticipation.builder()
                .academicPeriod(period)
                .user(teacher)
                .role(CourseRole.MAIN_PROFESSOR)
                .status(ParticipationStatus.ACTIVE)
                .build();

        participationRepository.save(participation);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "ASSIGN_TEACHER", "ACADEMIC_PERIOD", periodId,
                "Asignó a " + teacher.getEmail() + " al curso " + period.getId());
    }

    // --- GESTIÓN DE ESTADOS ---
    @Transactional
    public void requestClosure(Long periodId, String userEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        if (period.getStatus() != AcademicPeriodStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso no está activo.");
        }

        period.setStatus(AcademicPeriodStatus.CLOSURE_PENDING);
        academicPeriodRepository.save(period);
    }

    @Transactional
    public void approveClosure(Long periodId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        period.setStatus(AcademicPeriodStatus.FINISHED);
        academicPeriodRepository.save(period);
    }

    // --- LISTAS ---
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getPeriodsByTerm(Long termId) {
        if (!schoolTermRepository.existsById(termId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El semestre no existe");
        }
        List<AcademicPeriod> periods = academicPeriodRepository.findBySchoolTermId(termId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getPendingClosures() {
        List<AcademicPeriod> periods = academicPeriodRepository.findByStatus(AcademicPeriodStatus.CLOSURE_PENDING);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getAllPeriods() {
        List<AcademicPeriod> allPeriods = academicPeriodRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
        return allPeriods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- NUEVO MÉTODO: Obtener períodos académicos por curso ---
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getPeriodsByCourse(Long courseId) {
        // Validar si el curso existe antes de buscar sus períodos
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso no existe con ID: " + courseId);
        }
        List<AcademicPeriod> periods = academicPeriodRepository.findByCourseId(courseId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- MAPPER AUXILIAR (Mejorado para visualización) ---
    private AcademicPeriodSummaryDTO mapToDTO(AcademicPeriod period) {
        // Ahora contamos todos los participantes ACTIVO (Estudiantes y Profesores)
        Integer currentCount = participationRepository.countByAcademicPeriodIdAndStatus(
                period.getId(),
                ParticipationStatus.ACTIVE
        );

        // Lógica de visualización del horario (Para que el Admin sepa qué borrar)
        String scheduleText = "Sin Horario";
        if (period.getSchedule() != null) {
            scheduleText = String.format("%s %s - %s",
                    period.getSchedule().getDayOfWeek(),
                    period.getSchedule().getStartTime(),
                    period.getSchedule().getEndTime()
            );
        }

        return AcademicPeriodSummaryDTO.builder()
                .id(period.getId())
                .courseName(period.getCourse().getName())
                .courseCode(period.getCourse().getCode())
                .termName(period.getSchoolTerm().getName())
                .description(period.getCourse().getDescription())
                .status(period.getStatus().name())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .maxCapacity(period.getMaxCapacity())
                .currentEnrollment(currentCount)
                .schedule(scheduleText) // <--- CAMPO CLAVE VISUAL
                .build();
    }
}
