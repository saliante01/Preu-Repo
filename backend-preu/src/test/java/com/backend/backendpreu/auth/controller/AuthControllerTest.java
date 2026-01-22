package com.backend.backendpreu.auth.controller;

import com.backend.backendpreu.auth.dto.AuthResponseDTO;
import com.backend.backendpreu.auth.dto.LoginRequestDTO;
import com.backend.backendpreu.auth.dto.UserSummaryDTO; // Importante
import com.backend.backendpreu.auth.service.AuthService;
import com.backend.backendpreu.users.model.Role;
import jakarta.servlet.http.HttpServletResponse; // Importante
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDTO request;
    private AuthResponseDTO authServiceResponse;

    @BeforeEach
    void setUp() {
        request = new LoginRequestDTO();
        request.setEmail("admin@preu.cl");
        request.setPassword("1234");

        authServiceResponse = AuthResponseDTO.builder()
                .userId(1L)
                .email("admin@preu.cl")
                .fullName("Admin Sistema")
                .role(Role.ADMIN)
                .token("fake-jwt-token")
                .build();
    }

    @Test
    void login_exitoso() {
        when(authService.login(request)).thenReturn(authServiceResponse);

        ResponseEntity<UserSummaryDTO> result = authController.login(request, httpServletResponse);

        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals("admin@preu.cl", result.getBody().getEmail());
        assertEquals("ADMIN", result.getBody().getRole());
        // UserSummaryDTO no tiene método getToken(), así que no lo probamos aquí

        verify(httpServletResponse).addHeader(
                eq(HttpHeaders.SET_COOKIE),
                argThat(cookieString -> cookieString.contains("accessToken=fake-jwt-token"))
        );
    }

    @Test
    void login_password_incorrecta() {
        // GIVEN
        when(authService.login(request))
                .thenThrow(new RuntimeException("Invalid email or password"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authController.login(request, httpServletResponse)
        );

        assertEquals("Invalid email or password", ex.getMessage());

        verify(httpServletResponse, never()).addHeader(anyString(), anyString());
    }

    // 1. Test para: Usuario autenticado válido (Happy Path)
    @Test
    void getAuthenticatedUser_exitoso() {
        // GIVEN: Preparamos un usuario simulado que devolvería el servicio
        UserSummaryDTO userSummary = UserSummaryDTO.builder()
                .id(1L)
                .email("admin@preu.cl")
                .firstName("Admin")
                .lastName("Sistema")
                .role("ADMIN")
                .active(true)
                .build();


        when(authService.getAuthenticatedUser()).thenReturn(userSummary);


        ResponseEntity<UserSummaryDTO> result = authController.getAuthenticatedUser();


        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("admin@preu.cl", result.getBody().getEmail());
        assertEquals("ADMIN", result.getBody().getRole());
    }


    @Test
    void getAuthenticatedUser_noAutenticado() {

        when(authService.getAuthenticatedUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));


        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authController.getAuthenticatedUser()
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("User not authenticated", ex.getReason());
    }
}