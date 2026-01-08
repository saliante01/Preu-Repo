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

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_usuarioExiste() {

        User user = User.builder()
                .email("test-repo@preu.cl")
                .passwordHash("password123")
                .role(Role.ADMIN)
                .active(true)
                .firstName("Test")
                .lastName("Repository")
                .build();

        userRepository.save(user);


        Optional<User> result = userRepository.findByEmail("test-repo@preu.cl");


        assertTrue(result.isPresent(), "El usuario debería haber sido encontrado");
        assertEquals("test-repo@preu.cl", result.get().getEmail());
        assertEquals("Test", result.get().getFirstName());
    }

    @Test
    void findByEmail_usuarioNoExiste() {

        Optional<User> result = userRepository.findByEmail("fantasma@preu.cl");

        assertTrue(result.isEmpty(), "El resultado debería estar vacío");
    }
}