package com.backend.backendpreu.academicPeriod.service;

import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodCreateDTO;
import com.backend.backendpreu.academicPeriod.dto.AcademicPeriodSummaryDTO;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriodStatus;
import com.backend.backendpreu.academicPeriod.repository.AcademicPeriodRepository;
import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.courses.Repository.SchoolTermRepository;
import com.backend.backendpreu.courses.model.Course;
import com.backend.backendpreu.courses.model.SchoolTerm;
import com.backend.backendpreu.courses.Repository.CourseRepository;
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

    // --- LECTURA ---
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getAvailablePeriodsForStudent(Long studentId) {
        List<AcademicPeriod> periods = academicPeriodRepository.findAvailableForStudent(studentId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- CREACIÓN ---
    @Transactional
    public AcademicPeriod createPeriod(AcademicPeriodCreateDTO request, String adminEmail) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        SchoolTerm term = schoolTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semestre no encontrado"));

        // Validaciones de Fecha
        if (request.getStartDate().isBefore(term.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error: La fecha de inicio del curso no puede ser anterior al inicio del semestre.");
        }
        if (request.getEndDate().isAfter(term.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error: La fecha de fin del curso no puede ser posterior al fin del semestre.");
        }

        AcademicPeriod period = new AcademicPeriod();
        period.setCourse(course);
        period.setSchoolTerm(term);
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setStatus(AcademicPeriodStatus.ACTIVE);

        AcademicPeriod savedPeriod = academicPeriodRepository.save(period);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "OPEN_PERIOD", "ACADEMIC_PERIOD", savedPeriod.getId(),
                "Abrió curso " + course.getCode() + " en " + term.getName());

        return savedPeriod;
    }

    // --- GESTIÓN DE ESTADOS ---
    @Transactional
    public void requestClosure(Long periodId, String userEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        if (period.getStatus() != AcademicPeriodStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso ya está cerrado o en proceso de cierre.");
        }

        period.setStatus(AcademicPeriodStatus.CLOSURE_PENDING);
        academicPeriodRepository.save(period);

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        auditLogService.log(user, "REQUEST_CLOSURE", "ACADEMIC_PERIOD", periodId,
                "Solicitó finalizar el curso: " + period.getCourse().getName());
    }

    @Transactional
    public void approveClosure(Long periodId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        period.setStatus(AcademicPeriodStatus.FINISHED);
        academicPeriodRepository.save(period);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "FINISH_PERIOD", "ACADEMIC_PERIOD", periodId,
                "Finalizó oficialmente el curso: " + period.getCourse().getName());
    }

    // --- LISTAS ADMINISTRATIVAS ---
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

    // Método auxiliar privado
    private AcademicPeriodSummaryDTO mapToDTO(AcademicPeriod period) {
        return AcademicPeriodSummaryDTO.builder()
                .id(period.getId())
                .courseName(period.getCourse().getName())
                .courseCode(period.getCourse().getCode())
                .termName(period.getSchoolTerm().getName())
                .description(period.getCourse().getDescription())
                .status(period.getStatus().name())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .build();
    }
}