package com.backend.backendpreu.users.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents a system user within the academic intranet platform.
 * <p>
 * This entity centralizes identity, authentication, authorization,
 * and traceability for all people interacting with the system,
 * including students, professors, and administrators.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's first name.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Unique email address used for authentication.
     */
    @Column(nullable = false, length = 150)
    private String email;

    /**
     * Role assigned to the user within the system.
     * Defines access control and permissions.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Encrypted password hash.
     * Passwords are never stored in plain text.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Indicates whether the user account is active.
     * Inactive users are preserved for historical consistency.
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * Timestamp indicating when the user was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp indicating the last update of the user record.
     */
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Automatically sets creation and update timestamps
     * when the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    /**
     * Automatically updates the modification timestamp
     * whenever the entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
