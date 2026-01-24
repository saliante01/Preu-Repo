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

/**
 * Service class for managing user enrollments in academic periods.
 * Handles enrollment and unenrollment logic, including capacity validation and audit logging.
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final AuditLogService auditLogService;

    /**
     * Enrolls a user in a specific academic period.
     * Validates for duplicate enrollments, checks course capacity, and allows forced enrollment.
     * Logs the enrollment action.
     *
     * @param request The {@link EnrollmentRequestDTO} containing user, academic period, role, and force enrollment flag.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @throws ResponseStatusException if the admin, user, or academic period is not found,
     *                                 if the user is already enrolled, or if the course is full and forceEnroll is false.
     */
    @Transactional
    public void enrollUser(EnrollmentRequestDTO request, String adminEmail) {
        // 1. Find Admin (for Audit)
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        // 2. Find User to enroll
        User userToEnroll = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 3. Find Academic Period
        AcademicPeriod period = academicPeriodRepository.findById(request.getAcademicPeriodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic Period not found"));

        // 4. Validate Duplicates
        boolean exists = participationRepository.existsByAcademicPeriodIdAndUserId(period.getId(), userToEnroll.getId());
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enrolled in this course.");
        }

        // 5. 👇 CAPACITY AND OVER-ENROLLMENT VALIDATION (New Logic)
        Integer currentStudents = participationRepository.countByAcademicPeriodIdAndRole(
                period.getId(),
                CourseRole.STUDENT
        );

        // Check if full (or over capacity)
        if (currentStudents >= period.getMaxCapacity()) {
            // If force enrollment is NOT requested, throw an error
            if (!request.isForceEnroll()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "The course is full (" + currentStudents + "/" + period.getMaxCapacity() + "). " +
                                "Over-enrollment authorization is required (forceEnroll=true).");
            }
            // If forceEnroll = true, the code continues and allows enrollment (Extra seat)
        }

        // 6. Create Enrollment
        CourseParticipation participation = CourseParticipation.builder()
                .user(userToEnroll)
                .academicPeriod(period)
                .role(request.getRole())
                .status(ParticipationStatus.ACTIVE)
                .build();

        participationRepository.save(participation);

        // 7. Audit (Indicate if forced)
        String statusMsg = request.isForceEnroll() ? " (OVER-ENROLLMENT AUTHORIZED)" : "";

        String detailMessage = String.format("Enrollment created%s: %s in course ID %d (%d/%d)",
                statusMsg,
                userToEnroll.getEmail(),
                period.getId(),
                currentStudents + 1, // Estimated new total
                period.getMaxCapacity());

        auditLogService.log(
                adminUser,
                "ENROLL_USER",
                "COURSE_PARTICIPATION",
                period.getId(),
                detailMessage
        );
    }

    /**
     * Unenrolls a user from a specific academic period.
     * Logs the unenrollment action.
     *
     * @param academicPeriodId The ID of the academic period from which to unenroll the user.
     * @param userId The ID of the user to unenroll.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @throws ResponseStatusException if the admin or the specific enrollment is not found.
     */
    @Transactional
    public void unenrollUser(Long academicPeriodId, Long userId, String adminEmail) {
        // 1. Verify Admin (for audit)
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        // 2. Find the specific enrollment (Validate existence)
        CourseParticipation participation = participationRepository.findByAcademicPeriodIdAndUserId(academicPeriodId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment does not exist for this user in this period."));

        // 3. Delete
        participationRepository.delete(participation);

        // 4. Audit
        auditLogService.log(
                adminUser,
                "UNENROLL_USER",
                "COURSE_PARTICIPATION",
                participation.getId(),
                "Unenrolled user: " + participation.getUser().getEmail() + " (Role: " + participation.getRole() + ")"
        );
    }
}
