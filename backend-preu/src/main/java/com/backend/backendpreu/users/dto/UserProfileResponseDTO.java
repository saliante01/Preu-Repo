package com.backend.backendpreu.users.dto;

import com.backend.backendpreu.users.model.Role; // Assuming Role is needed for role mapping
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Data Transfer Object for a comprehensive user profile response.
 * Includes basic user information, activity status, timestamps, and academic history.
 */
@Data
@Builder
public class UserProfileResponseDTO {
    /**
     * The unique identifier of the user.
     */
    private Long id;
    /**
     * The first name of the user.
     */
    private String firstName;
    /**
     * The last name of the user.
     */
    private String lastName;
    /**
     * The email address of the user.
     */
    private String email;
    /**
     * The role of the user (e.g., ADMIN, TEACHER, STUDENT) as a String.
     */
    private String role;
    /**
     * Indicates whether the user account is active.
     */
    private Boolean active;
    /**
     * The timestamp when the user account was created.
     */
    private OffsetDateTime createdAt; // Added missing field
    /**
     * The timestamp of the last update to the user account.
     */
    private OffsetDateTime updatedAt; // Added missing field

    /**
     * A list of {@link AcademicHistoryDTO} representing the user's academic participation.
     * Applicable for both students and professors.
     */
    private List<AcademicHistoryDTO> academicHistory;
}
