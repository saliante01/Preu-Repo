package com.backend.backendpreu.audit.controller;

import com.backend.backendpreu.audit.dto.AuditLogResponseDTO;
import com.backend.backendpreu.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for retrieving audit logs.
 * Provides endpoints for administrators to query and view system audit trails.
 */
@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Retrieves a list of audit logs based on optional filters.
     * Only accessible by users with the 'ADMIN' role.
     *
     * @param userId The ID of the user (actor) who performed the action (optional).
     * @param action The specific action performed (e.g., "CREATE_USER", "LOGIN") (optional).
     * @param entityName The name of the entity affected (e.g., "USER", "COURSE") (optional).
     * @return A {@link ResponseEntity} containing a list of {@link AuditLogResponseDTO}.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityName
    ) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(userId, action, entityName));
    }
}
