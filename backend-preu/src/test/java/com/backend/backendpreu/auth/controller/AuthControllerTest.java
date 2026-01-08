package com.backend.backendpreu.auth.controller;

import com.backend.backendpreu.auth.dto.AuthResponseDTO;
import com.backend.backendpreu.auth.dto.LoginRequestDTO;
import com.backend.backendpreu.auth.service.AuthService;
import com.backend.backendpreu.users.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDTO request;
    private AuthResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("1234");

        response = AuthResponseDTO.builder()
                .userId(1L)
                .email("admin@preu.cl")
                .fullName("Admin Sistema")
                .role(Role.ADMIN)
                .token("fake-jwt-token")
                .build();
    }

    @Test
    void login_exitoso() {
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<AuthResponseDTO> result = authController.login(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("fake-jwt-token", result.getBody().getToken());
        assertEquals(Role.ADMIN, result.getBody().getRole());
    }

    @Test
    void login_password_incorrecta() {
        when(authService.login(request))
                .thenThrow(new RuntimeException("Invalid email or password"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        assertEquals("Invalid email or password", ex.getMessage());
    }
}
