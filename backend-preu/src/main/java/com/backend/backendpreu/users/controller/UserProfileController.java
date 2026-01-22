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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository; // <--- INYECTAR REPOSITORIO

    // 1. GET /api/users/me/profile
    @GetMapping("/users/me/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        Long currentUserId = extractIdFromAuth(authentication);
        return ResponseEntity.ok(userProfileService.getUserProfile(currentUserId));
    }

    // 2. GET /api/admin/users/{userId}/profile
    @GetMapping("/admin/users/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDTO> getUserProfileByAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    private Long extractIdFromAuth(Authentication auth) {
        String email = auth.getName(); // Spring Security guarda el email en getName()
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))
                .getId();
    }
}