package com.backend.backendpreu.audit.model;

import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Records relevant actions performed within the system for auditing purposes.
 *
 * <p>This entity is used for traceability, security auditing,
 * and operational diagnostics.</p>
 *
 * <p>The associated user may be null for system-triggered actions.</p>
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who performed the action.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Action performed (e.g. LOGIN, CREATE_EVALUATION).
     */
    @Column(nullable = false)
    private String action;

    /**
     * Name of the affected entity.
     */
    @Column(nullable = false)
    private String entity;

    /**
     * Identifier of the affected entity instance.
     */
    @Column(name = "entity_id")
    private Long entityId;

    /**
     * Timestamp when the action occurred.
     */
    @Column(name = "timestamp", nullable = false, updatable = false)
    private OffsetDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = OffsetDateTime.now();
    }
}
