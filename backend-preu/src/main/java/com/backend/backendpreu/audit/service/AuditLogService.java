package com.backend.backendpreu.audit.service;

import com.backend.backendpreu.audit.dto.AuditLogResponseDTO;
import com.backend.backendpreu.audit.model.AuditLog;
import com.backend.backendpreu.audit.repository.AuditLogRepository;
import com.backend.backendpreu.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Registra una acción en el sistema.
     * @param actor El usuario que realiza la acción.
     * @param action El código de la acción (ej: CREATE_USER).
     * @param entityName El nombre de la entidad afectada (ej: USER).
     * @param entityId El ID de la entidad afectada.
     * @param details Descripción legible de lo que ocurrió.
     */
    public void log(User actor, String action, String entityName, Long entityId, String details) {

        AuditLog auditLog = AuditLog.builder()
                .user(actor)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details) // Guardamos el detalle
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponseDTO> getAuditLogs(Long userId, String action, String entityName) {

        // Llamamos al repo con los filtros
        List<AuditLog> logs = auditLogRepository.searchAuditLogs(userId, action, entityName);

        // Convertimos Entidad -> DTO
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