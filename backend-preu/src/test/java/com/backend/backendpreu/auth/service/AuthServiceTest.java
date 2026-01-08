package com.backend.backendpreu.auth.service;

import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.auth.dto.AuthResponseDTO;
import com.backend.backendpreu.auth.dto.LoginRequestDTO;
import com.backend.backendpreu.auth.security.JwtService;
import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .id(1L)
                .email("admin@preu.cl")
                .passwordHash("hashed-password")
                .firstName("Admin")
                .lastName("Sistema")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }


    @Test
    void login_success() {
        LoginRequestDTO request =
                new LoginRequestDTO("admin@preu.cl", "password");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("password", "hashed-password"))
                .thenReturn(true);

        when(jwtService.generateToken(activeUser))
                .thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("admin@preu.cl", response.getEmail());
        assertEquals("Admin Sistema", response.getFullName());
        assertEquals(Role.ADMIN, response.getRole());
        assertEquals("jwt-token", response.getToken());

        verify(auditLogService, times(1))
                .log(activeUser, "LOGIN", "USER", 1L);
    }


    @Test
    void login_invalidPassword_throwsException() {
        LoginRequestDTO request =
                new LoginRequestDTO("admin@preu.cl", "wrong-password");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid password", ex.getMessage());

        verify(jwtService, never()).generateToken(any());
        verify(auditLogService, never()).log(any(), any(), any(), any());
    }


    @Test
    void login_inactiveUser_throwsException() {
        activeUser.setActive(false);

        LoginRequestDTO request =
                new LoginRequestDTO("admin@preu.cl", "password");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(activeUser));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("User inactive", ex.getMessage());

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
        verify(auditLogService, never()).log(any(), any(), any(), any());
    }


    @Test
    void login_success_auditIsSaved() {
        LoginRequestDTO request =
                new LoginRequestDTO("admin@preu.cl", "password");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        when(jwtService.generateToken(activeUser))
                .thenReturn("jwt-token");

        authService.login(request);

        verify(auditLogService).log(
                eq(activeUser),
                eq("LOGIN"),
                eq("USER"),
                eq(1L)
        );
    }
}
