package com.backend.backendpreu.audit.service;

import com.backend.backendpreu.audit.model.AuditLog;
import com.backend.backendpreu.audit.repository.AuditLogRepository;
import com.backend.backendpreu.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String entity, Long entityId) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .build();

        auditLogRepository.save(log);
    }
}

