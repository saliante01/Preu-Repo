package com.backend.backendpreu.audit.repository;

import com.backend.backendpreu.audit.model.AuditLog;
import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_auditLogCorrectamente() {

        User user = User.builder()
                .email("audit-test@preu.cl")
                .passwordHash("hashed-password")
                .role(Role.ADMIN)
                .active(true)
                .firstName("Audit")
                .lastName("Tester")
                .build();

        user = userRepository.save(user);


        AuditLog log = AuditLog.builder()
                .user(user)
                .action("LOGIN")
                .entityName("USER")
                .entityId(user.getId())
                .details("Prueba de integración de repositorio")
                .timestamp(LocalDateTime.now())
                .build();

        // 3. Guardamos el Log
        AuditLog saved = auditLogRepository.save(log);

        // 4. Verificaciones
        assertNotNull(saved.getId(), "El ID del log no debería ser nulo");
        assertEquals("LOGIN", saved.getAction());
        assertEquals("USER", saved.getEntityName());
        assertEquals(user.getId(), saved.getEntityId());
        assertEquals("Prueba de integración de repositorio", saved.getDetails()); 
        assertNotNull(saved.getTimestamp(), "El timestamp no debería ser nulo");
    }
}