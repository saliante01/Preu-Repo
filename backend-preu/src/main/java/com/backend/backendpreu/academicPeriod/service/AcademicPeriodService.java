package com.backend.backendpreu.academicPeriod.service;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import com.backend.backendpreu.academicPaticipation.repository.CourseParticipationRepository;
import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodCreateDTO;
import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodSummaryDTO;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriodStatus;
import com.backend.backendpreu.academicPeriod.model.ClassSchedule; // Importante
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

    // --- CREACIÓN (Ahora con Horario y Capacidad) ---
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

        // Capacidad (Default 30)
        period.setMaxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : 30);

        // 4. Crear Horario (Si viene en el request)
        if (request.getDayOfWeek() != null && request.getStartTime() != null && request.getEndTime() != null) {
            ClassSchedule schedule = ClassSchedule.builder()
                    .dayOfWeek(request.getDayOfWeek())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .build();
            period.setSchedule(schedule); // Se guardará por CascadeType.ALL
        }

        AcademicPeriod savedPeriod = academicPeriodRepository.save(period);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "OPEN_PERIOD", "ACADEMIC_PERIOD", savedPeriod.getId(),
                "Abrió curso " + course.getCode() + " (Cupo: " + period.getMaxCapacity() + ")");

        return savedPeriod;
    }

    // --- ASIGNAR PROFESOR (NUEVO) ---
    @Transactional
    public void assignTeacherToPeriod(Long periodId, Long teacherId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado"));

        // Validar si ya está asignado
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

    // --- MAPPER AUXILIAR ---
    private AcademicPeriodSummaryDTO mapToDTO(AcademicPeriod period) {
        Integer currentCount = participationRepository.countByAcademicPeriodIdAndRole(
                period.getId(),
                CourseRole.STUDENT
        );

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
                .build();
    }
}