package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.users.dto.UserProfileDTO;
import com.backend.backendpreu.users.service.UserProfileService;
import com.backend.backendpreu.users.repository.UserRepository; // <--- IMPORTAR
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controlador para gestión de perfiles de usuario.
 *
 * Permite:
 * - Que un usuario vea su propio perfil
 * - Que un ADMIN vea el perfil de cualquier usuario
 *
 * Ruta base:
 * /api
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

    /**
     * Servicio de perfiles de usuario
     */
    private final UserProfileService userProfileService;

    /**
     * Repositorio de usuarios para resolver identidad del usuario autenticado
     */
    private final UserRepository userRepository;

    /**
     * Obtiene el perfil del usuario actualmente autenticado.
     *
     * Endpoint:
     * GET /api/users/me/profile
     */
    @GetMapping("/users/me/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        Long currentUserId = extractIdFromAuth(authentication);
        return ResponseEntity.ok(userProfileService.getUserProfile(currentUserId));
    }

    /**
     * Obtiene el perfil de cualquier usuario (solo ADMIN).
     *
     * Endpoint:
     * GET /api/admin/users/{userId}/profile
     */
    @GetMapping("/admin/users/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDTO> getUserProfileByAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    /**
     * Extrae el ID del usuario autenticado usando su email.
     */
    private Long extractIdFromAuth(Authentication auth) {
        String email = auth.getName(); // Spring Security guarda el email en getName()
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))
                .getId();
    }
}
