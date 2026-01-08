package com.backend.backendpreu.user.repository;

import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest; // Cambiado
import org.springframework.transaction.annotation.Transactional; // Importante

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // <--- Usamos esto en lugar de @DataJpaTest
@Transactional  // <--- Importante: Revierte los cambios en la DB al terminar cada test
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_usuarioExiste() {
        // 1. Crear y guardar un usuario
        User user = User.builder()
                .email("test-repo@preu.cl")
                .passwordHash("password123")
                .role(Role.ADMIN)
                .active(true)
                .firstName("Test")
                .lastName("Repository")
                .build();

        userRepository.save(user);

        // 2. Buscarlo por email
        Optional<User> result = userRepository.findByEmail("test-repo@preu.cl");

        // 3. Verificar
        assertTrue(result.isPresent(), "El usuario debería haber sido encontrado");
        assertEquals("test-repo@preu.cl", result.get().getEmail());
        assertEquals("Test", result.get().getFirstName());
    }

    @Test
    void findByEmail_usuarioNoExiste() {
        // Buscar un email que no hemos guardado
        Optional<User> result = userRepository.findByEmail("fantasma@preu.cl");

        assertTrue(result.isEmpty(), "El resultado debería estar vacío");
    }
}