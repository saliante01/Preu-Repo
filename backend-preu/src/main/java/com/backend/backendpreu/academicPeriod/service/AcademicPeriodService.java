package com.backend.backendpreu.academicPeriod.service;

import com.backend.backendpreu.courses.dto.CourseParticipantDTO;
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

/**
 * Service class for managing academic periods.
 * Handles business logic related to creating, retrieving, updating, and deleting
 * academic periods, including enrollment and teacher assignment.
 */
@Service
@RequiredArgsConstructor
public class AcademicPeriodService {

    private final AcademicPeriodRepository academicPeriodRepository;
    private final CourseRepository courseRepository;
    private final SchoolTermRepository schoolTermRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final CourseParticipationRepository participationRepository;

    /**
     * Retrieves a list of academic periods available for a specific student to enroll in.
     * This includes periods that are active, belong to an active school term, and where the student is not yet enrolled.
     *
     * @param studentId The ID of the student.
     * @return A list of {@link AcademicPeriodSummaryDTO} representing available periods.
     */
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getAvailablePeriodsForStudent(Long studentId) {
        List<AcademicPeriod> periods = academicPeriodRepository.findAvailableForStudent(studentId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a single academic period by its ID.
     *
     * @param id The ID of the academic period.
     * @return An {@link AcademicPeriodSummaryDTO} if found.
     * @throws ResponseStatusException if the academic period is not found.
     */
    @Transactional(readOnly = true)
    public AcademicPeriodSummaryDTO getPeriodById(Long id) {
        AcademicPeriod period = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found"));
        return mapToDTO(period);
    }

    /**
     * Retrieves a list of participants (students and professors) for a specific academic period.
     *
     * @param periodId The ID of the academic period.
     * @return A list of {@link CourseParticipantDTO} representing active participants.
     */
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

    /**
     * Creates a new academic period.
     * Performs validations on the course, school term, and dates.
     * Also checks for duplicate schedules for the same course and term.
     *
     * @param request The DTO containing the data for the new academic period.
     * @param adminEmail The email of the administrator performing the action for audit logging.
     * @return The created {@link AcademicPeriod} entity.
     * @throws ResponseStatusException if the course or term is not found, dates are invalid,
     *                                 or a duplicate schedule exists.
     */
    @Transactional
    public AcademicPeriod createPeriod(AcademicPeriodCreateDTO request, String adminEmail) {
        // 1. Validate Course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        // 2. Validate School Term
        SchoolTerm term = schoolTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School Term not found"));

        // 3. Validate Dates
        if (request.getStartDate().isBefore(term.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course start date cannot be before term start date");
        }
        if (request.getEndDate().isAfter(term.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course end date cannot be after term end date");
        }

        AcademicPeriod period = new AcademicPeriod();
        period.setCourse(course);
        period.setSchoolTerm(term);
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setStatus(AcademicPeriodStatus.ACTIVE);
        period.setMaxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : 30);

        // 4. Validate Duplicate Schedule (if a schedule is provided)
        if (request.getDayOfWeek() != null && request.getStartTime() != null && request.getEndTime() != null) {
            boolean existsDuplicateSchedule = academicPeriodRepository.existsByCourseIdAndSchoolTermIdAndSchedule_DayOfWeekAndSchedule_StartTimeAndSchedule_EndTime(
                    request.getCourseId(),
                    request.getTermId(),
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime()
            );

            if (existsDuplicateSchedule) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "An academic period with the same course, term, day, and schedule already exists.");
            }
        }

        // 5. Create Schedule
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
                "Opened course " + course.getCode() + " (Capacity: " + period.getMaxCapacity() + ")");

        return savedPeriod;
    }

    /**
     * Updates an existing academic period.
     * Allows modification of start/end dates, max capacity, and schedule.
     *
     * @param periodId The ID of the academic period to update.
     * @param request The DTO containing the updated data.
     * @param adminEmail The email of the administrator performing the action for audit logging.
     * @return The updated {@link AcademicPeriod} entity.
     * @throws ResponseStatusException if the academic period is not found.
     */
    @Transactional
    public AcademicPeriod updatePeriod(Long periodId, AcademicPeriodCreateDTO request, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found"));

        // Update basic data
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        if(request.getMaxCapacity() != null) {
            period.setMaxCapacity(request.getMaxCapacity());
        }

        // Update Schedule
        if (request.getDayOfWeek() != null) {
            if (period.getSchedule() == null) {
                period.setSchedule(new ClassSchedule());
            }
            period.getSchedule().setDayOfWeek(request.getDayOfWeek());
            period.getSchedule().setStartTime(request.getStartTime());
            period.getSchedule().setEndTime(request.getEndTime());
        }

        // Audit log
        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "UPDATE_PERIOD", "ACADEMIC_PERIOD", periodId, "Updated schedule/dates");

        return academicPeriodRepository.save(period);
    }

    /**
     * Deletes an academic period by its ID.
     * Prevents deletion if there are active student enrollments.
     *
     * @param periodId The ID of the academic period to delete.
     * @param adminEmail The email of the administrator performing the action for audit logging.
     * @throws ResponseStatusException if the academic period is not found or has active students.
     */
    @Transactional
    public void deletePeriod(Long periodId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found"));

        // Security: Do not delete if there are students enrolled
        Integer enrollmentCount = participationRepository.countByAcademicPeriodIdAndRole(
                periodId,
                CourseRole.STUDENT
        );

        if (enrollmentCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete: There are " + enrollmentCount + " enrolled students. Unenroll them first.");
        }

        academicPeriodRepository.delete(period);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "DELETE_PERIOD", "ACADEMIC_PERIOD", periodId,
                "Deleted course schedule ID: " + periodId);
    }

    /**
     * Assigns a teacher to an academic period.
     * Ensures the teacher exists and is not already assigned to the period.
     *
     * @param periodId The ID of the academic period.
     * @param teacherId The ID of the teacher to assign.
     * @param adminEmail The email of the administrator performing the action for audit logging.
     * @throws ResponseStatusException if the period or teacher is not found, or the teacher is already assigned.
     */
    @Transactional
    public void assignTeacherToPeriod(Long periodId, Long teacherId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

        boolean alreadyAssigned = participationRepository.existsByAcademicPeriodIdAndUserId(periodId, teacherId);
        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This teacher is already assigned.");
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
                "Assigned " + teacher.getEmail() + " to course " + period.getId());
    }

    /**
     * Requests the closure of an academic period.
     * Changes the period status to {@link AcademicPeriodStatus#CLOSURE_PENDING}.
     *
     * @param periodId The ID of the academic period.
     * @param userEmail The email of the user (admin or teacher) requesting the closure.
     * @throws ResponseStatusException if the period is not found or not active.
     */
    @Transactional
    public void requestClosure(Long periodId, String userEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found"));

        if (period.getStatus() != AcademicPeriodStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The course is not active.");
        }

        period.setStatus(AcademicPeriodStatus.CLOSURE_PENDING);
        academicPeriodRepository.save(period);

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        auditLogService.log(user,"REQUEST_CLOSURE","ACADEMIC_PERIOD",periodId,"Requested course closure");
    }

    /**
     * Approves the closure of an academic period.
     * Changes the period status to {@link AcademicPeriodStatus#FINISHED}.
     *
     * @param periodId The ID of the academic period.
     * @param adminEmail The email of the administrator approving the closure.
     * @throws ResponseStatusException if the period is not found.
     */
    @Transactional
    public void approveClosure(Long periodId, String adminEmail) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found"));

        period.setStatus(AcademicPeriodStatus.FINISHED);
        academicPeriodRepository.save(period);
        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin,"ClOSE_PERIOD","ACADEMIC_PERIOD",periodId,"Approved closure and finalized the course");
    }

    /**
     * Retrieves all academic periods associated with a given school term.
     *
     * @param termId The ID of the school term.
     * @return A list of {@link AcademicPeriodSummaryDTO} for the specified term.
     * @throws ResponseStatusException if the school term does not exist.
     */
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getPeriodsByTerm(Long termId) {
        if (!schoolTermRepository.existsById(termId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The school term does not exist");
        }
        List<AcademicPeriod> periods = academicPeriodRepository.findBySchoolTermId(termId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a list of academic periods that are in the {@link AcademicPeriodStatus#CLOSURE_PENDING} state.
     *
     * @return A list of {@link AcademicPeriodSummaryDTO} for periods pending closure.
     */
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getPendingClosures() {
        List<AcademicPeriod> periods = academicPeriodRepository.findByStatus(AcademicPeriodStatus.CLOSURE_PENDING);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves all academic periods, ordered by start date in descending order.
     *
     * @return A list of all {@link AcademicPeriodSummaryDTO}.
     */
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getAllPeriods() {
        List<AcademicPeriod> allPeriods = academicPeriodRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));
        return allPeriods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves all academic periods associated with a given course ID.
     *
     * @param courseId The ID of the course.
     * @return A list of {@link AcademicPeriodSummaryDTO} for the specified course.
     * @throws ResponseStatusException if the course does not exist.
     */
    @Transactional(readOnly = true)
    public List<AcademicPeriodSummaryDTO> getPeriodsByCourse(Long courseId) {
        // Validate if the course exists before searching its periods
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId);
        }
        List<AcademicPeriod> periods = academicPeriodRepository.findByCourseId(courseId);
        return periods.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Maps an {@link AcademicPeriod} entity to an {@link AcademicPeriodSummaryDTO}.
     * Includes logic to determine current enrollment count and format the schedule for display.
     *
     * @param period The {@link AcademicPeriod} entity to map.
     * @return The resulting {@link AcademicPeriodSummaryDTO}.
     */
    private AcademicPeriodSummaryDTO mapToDTO(AcademicPeriod period) {
        Integer currentCount = participationRepository.countByAcademicPeriodIdAndRole(
                period.getId(),
                CourseRole.STUDENT
        );

        // Schedule display logic (for Admin to know what to delete)
        String scheduleText = "No Schedule";
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
                .schedule(scheduleText)
                .build();
    }
}
