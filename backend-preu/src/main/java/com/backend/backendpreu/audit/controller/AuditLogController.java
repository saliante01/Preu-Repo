package com.backend.backendpreu.audit.controller;

import com.backend.backendpreu.audit.dto.AuditLogResponseDTO;
import com.backend.backendpreu.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔐 Solo Admins
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityName
    ) {
        // Pasamos los parámetros (que pueden ser null) al servicio
        return ResponseEntity.ok(auditLogService.getAuditLogs(userId, action, entityName));
    }
}