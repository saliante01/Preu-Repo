package com.backend.backendpreu.users.model;

import com.backend.backendpreu.courses.model.Subject;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
// 1. CAMBIO IMPORTANTE: Implementamos UserDetails
public class User implements UserDetails {

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name= "user_subjects",
            joinColumns = @JoinColumn(name="user_id")
    )
    @Column(name="subject_name")
    @Enumerated(EnumType.STRING) // Guardamos el nombre del ENUM (ej: "MATHEMATICS")
    private Set<Subject> subjects = new HashSet<>();
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        // Spring espera 'getPassword', tú tienes 'passwordHash'
        return passwordHash;
    }

    @Override
    public String getUsername() {
        // Spring espera 'getUsername', tú usas el 'email' como usuario
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta no se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        // Conectamos esto con tu campo 'active'.
        // Si active es false, Spring NO dejará loguear al usuario.
        return active;
    }
}