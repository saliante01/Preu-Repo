package com.backend.backendpreu.audit.service;

import com.backend.backendpreu.audit.model.AuditLog;
import com.backend.backendpreu.audit.repository.AuditLogRepository;
import com.backend.backendpreu.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}