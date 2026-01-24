package com.backend.backendpreu.courses.dto;

import ch.qos.logback.core.status.Status;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for course participant information.
 * Used to display details about a user's participation in a course.
 */
@Data
@Builder
public class CourseParticipantDTO {
    /**
     * The ID of the participating user.
     */
    private Long userId;
    /**
     * The full name of the participating user.
     */
    private String fullName;
    /**
     * The email of the participating user.
     */
    private String email;
    /**
     * The role of the user in the course (e.g., "MAIN_PROFESSOR", "STUDENT").
     */
    private String role;
    /**
     * The status of the participation.
     */
    private Status status;
}
