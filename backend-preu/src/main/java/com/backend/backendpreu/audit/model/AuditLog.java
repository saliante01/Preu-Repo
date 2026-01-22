package com.backend.backendpreu.audit.model;

import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents an audit log entry, recording significant actions performed within the system.
 * <p>
 * Each log entry captures details about who performed an action, what action was taken,
 * which entity was affected, and when it occurred.
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    /**
     * Unique identifier of the audit log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The {@link User} who performed the action (the actor).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The specific action performed (e.g., "CREATE_USER", "UPDATE_STATUS", "DELETE").
     */
    @Column(nullable = false)
    private String action;

    /**
     * The name of the entity affected by the action (e.g., "USER", "COURSE", "ACADEMIC_PERIOD").
     */
    @Column(nullable = false)
    private String entityName;

    /**
     * The ID of the affected entity.
     */
    @Column(nullable = false)
    private Long entityId;

    /**
     * Detailed, human-readable description of the action (e.g., "Created user pepe@gmail.com with role USER").
     */
    @Column(length = 1000)
    private String details;

    /**
     * Timestamp when the action occurred.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
