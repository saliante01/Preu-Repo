package com.backend.backendpreu.audit.service;

import com.backend.backendpreu.audit.dto.AuditLogResponseDTO;
import com.backend.backendpreu.audit.model.AuditLog;
import com.backend.backendpreu.audit.repository.AuditLogRepository;
import com.backend.backendpreu.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing audit logs.
 * Provides functionality to record and retrieve system actions for auditing purposes.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Records an action performed within the system.
     *
     * @param actor The {@link User} who performed the action.
     * @param action The code representing the action (e.g., "CREATE_USER", "LOGIN").
     * @param entityName The name of the entity affected by the action (e.g., "USER", "COURSE").
     * @param entityId The ID of the affected entity.
     * @param details A human-readable description of what occurred.
     */
    @Transactional
    public void log(User actor, String action, String entityName, Long entityId, String details) {

        AuditLog auditLog = AuditLog.builder()
                .user(actor)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    /**
     * Retrieves a list of audit logs, with optional filtering by user ID, action, and entity name.
     * The results are mapped to {@link AuditLogResponseDTO}.
     *
     * @param userId The ID of the user (actor) to filter by. Can be {@code null}.
     * @param action The action code to filter by. Can be {@code null}.
     * @param entityName The entity name to filter by. Can be {@code null}.
     * @return A list of {@link AuditLogResponseDTO} representing the filtered audit logs.
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getAuditLogs(Long userId, String action, String entityName) {

        List<AuditLog> logs = auditLogRepository.searchAuditLogs(userId, action, entityName);

        return logs.stream()
                .map(log -> AuditLogResponseDTO.builder()
                        .id(log.getId())
                        .actorName(log.getUser().getFirstName() + " " + log.getUser().getLastName())
                        .role(log.getUser().getRole().name())
                        .action(log.getAction())
                        .entityName(log.getEntityName())
                        .entityId(log.getEntityId())
                        .details(log.getDetails())
                        .timestamp(log.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

}
