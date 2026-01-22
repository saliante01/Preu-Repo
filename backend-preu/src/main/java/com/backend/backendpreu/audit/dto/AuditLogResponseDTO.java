package com.backend.backendpreu.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for representing an audit log entry.
 * Provides a summarized view of a system action,
 * including who performed it, what was done, and when.
 */
@Data
@Builder
public class AuditLogResponseDTO {
    /**
     * Unique identifier of the audit log entry.
     */
    private Long id;
    /**
     * Full name of the user who performed the action.
     */
    private String actorName;
    /**
     * Role of the user who performed the action.
     */
    private String role;
    /**
     * The specific action that was performed (e.g., "CREATE_USER", "LOGIN").
     */
    private String action;
    /**
     * The name of the entity that was affected by the action (e.g., "USER", "COURSE").
     */
    private String entityName;
    /**
     * The ID of the entity that was affected.
     */
    private Long entityId;
    /**
     * A detailed, human-readable description of the action.
     */
    private String details;
    /**
     * The timestamp when the action occurred.
     */
    private LocalDateTime timestamp;
}
