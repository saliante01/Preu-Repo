package com.backend.backendpreu.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponseDTO {
    private Long id;
    private String actorName;
    private String role;
    private String action;
    private String entityName;
    private Long entityId;
    private String details;
    private LocalDateTime timestamp;
}
