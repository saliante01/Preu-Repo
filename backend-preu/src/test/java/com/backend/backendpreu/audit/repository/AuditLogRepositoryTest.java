package com.backend.backendpreu.audit.repository;

import com.backend.backendpreu.audit.model.AuditLog;
import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest; // <--- CAMBIO 1
import org.springframework.transaction.annotation.Transactional; // <--- CAMBIO 2

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // <--- Usamos el contexto completo para evitar errores de librerías faltantes
@Transactional  // <--- Importante: Borra los datos al terminar el test
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_auditLogCorrectamente() {
        // 1. Primero necesitamos un Usuario real guardado en BD
        User user = User.builder()
                .email("audit-test@preu.cl")
                .passwordHash("hashed-password")
                .role(Role.ADMIN)
                .active(true)
                .firstName("Audit")
                .lastName("Tester")
                .build();

        user = userRepository.save(user);

        // 2. Creamos el Log asociado a ese usuario
        AuditLog log = AuditLog.builder()
                .user(user)
                .action("LOGIN")
                .entity("USER") // Asegúrate que en tu modelo se llame 'entity' o 'targetEntity'
                .entityId(user.getId())
                .build();

        // 3. Guardamos el Log
        AuditLog saved = auditLogRepository.save(log);

        // 4. Verificaciones
        assertNotNull(saved.getId(), "El ID del log no debería ser nulo");
        assertEquals("LOGIN", saved.getAction());
        assertEquals("USER", saved.getEntity()); // Ojo aquí con el nombre del getter
        assertEquals(user.getId(), saved.getEntityId());
        assertNotNull(saved.getTimestamp(), "El timestamp debería generarse automáticamente");
    }
}