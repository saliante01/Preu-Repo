package com.backend.backendpreu.auth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for a summarized view of user information.
 * Used to return essential user details after authentication or when fetching basic profile data.
 */
@Data
@Builder
public class UserSummaryDTO {

    /**
     * Unique identifier of the user.
     */
    private Long id;
    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The user's email address.
     */
    private String email;
    /**
     * The role of the user within the system (e.g., "ADMIN", "STUDENT", "PROFESSOR").
     */
    private String role;
    /**
     * Indicates whether the user's account is active.
     */
    private Boolean active;
}
