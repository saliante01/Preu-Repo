package com.backend.backendpreu.courses.dto;

import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import lombok.Data;

/**
 * Data Transfer Object for requesting user enrollment in an academic period.
 * Includes options for assigning roles and forcing enrollment.
 */
@Data
public class EnrollmentRequestDTO {
    /**
     * The ID of the user to enroll.
     */
    private Long userId;
    /**
     * The ID of the academic period in which to enroll the user.
     */
    private Long academicPeriodId;
    /**
     * The role the user will have in the academic period (e.g., STUDENT, MAIN_PROFESSOR).
     */
    private CourseRole role;
    /**
     * A flag indicating whether to bypass capacity checks and force the enrollment.
     */
    private boolean forceEnroll;
}
