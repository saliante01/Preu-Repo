package com.backend.backendpreu.auth.service;

import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.auth.dto.AuthResponseDTO;
import com.backend.backendpreu.auth.dto.LoginRequestDTO;
import com.backend.backendpreu.auth.dto.UserSummaryDTO;
import com.backend.backendpreu.auth.security.JwtService;
import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

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

    @Mock // <--- 1. AGREGAMOS EL MOCK DEL NUEVO SERVICIO
    private CaptchaService captchaService;

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
        // <--- 2. AGREGAMOS EL TOKEN AL CONSTRUCTOR (O usa setters si no tienes constructor)
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("password");
        request.setCaptchaToken("valid-token");

        // <--- 3. SIMULAMOS QUE EL CAPTCHA ES VÁLIDO
        when(captchaService.verify("valid-token")).thenReturn(true);

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("password", "hashed-password"))
                .thenReturn(true);

        when(jwtService.generateToken(activeUser))
                .thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("jwt-token", response.getToken());

        verify(auditLogService, times(1))
                .log(activeUser, "LOGIN", "USER", 1L);
    }

    // --- NUEVO TEST IMPORTANTE ---
    @Test
    void login_invalidCaptcha_throwsException() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("password");
        request.setCaptchaToken("invalid-token");

        // Simulamos que el Captcha falló
        when(captchaService.verify("invalid-token")).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        // Verificamos el mensaje de error
        assertTrue(ex.getMessage().contains("Captcha"));

        // Verificamos que NUNCA llamó a la base de datos (seguridad)
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_invalidPassword_throwsException() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("wrong-password");
        request.setCaptchaToken("valid-token");

        // El captcha debe pasar para llegar a validar la password
        when(captchaService.verify("valid-token")).thenReturn(true); // <--- AGREGADO

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
    }

    @Test
    void login_inactiveUser_throwsException() {
        activeUser.setActive(false);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("password");
        request.setCaptchaToken("valid-token");

        // El captcha debe pasar
        when(captchaService.verify("valid-token")).thenReturn(true); // <--- AGREGADO

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(activeUser));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("User inactive", ex.getMessage());
    }

    @Test
    void login_success_auditIsSaved() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("password");
        request.setCaptchaToken("valid-token");

        when(captchaService.verify("valid-token")).thenReturn(true); // <--- AGREGADO

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

    // Los tests de 'getAuthenticatedUser' no cambian porque ese método no usa Captcha
    @Test
    void getAuthenticatedUser_success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("somePrincipal");
        when(authentication.getName()).thenReturn("admin@preu.cl");

        when(userRepository.findByEmail("admin@preu.cl"))
                .thenReturn(Optional.of(activeUser));

        UserSummaryDTO result = authService.getAuthenticatedUser();

        assertNotNull(result);
        assertEquals("admin@preu.cl", result.getEmail());
    }

    @Test
    void getAuthenticatedUser_notAuthenticated_throwsException() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.getAuthenticatedUser()
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}