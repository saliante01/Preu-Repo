package com.backend.backendpreu.auth.dto;
import com.backend.backendpreu.users.model.Role;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for authentication response.
 * Contains user identification, JWT token, and basic profile information
 * returned upon successful login.
 */
@Data
@Builder
public class AuthResponseDTO {
    /**
     * The unique identifier of the authenticated user.
     */
    private Long userId;
    /**
     * The JSON Web Token (JWT) for subsequent authenticated requests.
     */
    private String token;
    /**
     * The email address of the authenticated user.
     */
    private String email;
    /**
     * The full name (first name + last name) of the authenticated user.
     */
    private String fullName;
    /**
     * The {@link Role} of the authenticated user.
     */
    private Role role;

}
