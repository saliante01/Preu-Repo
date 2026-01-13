package com.backend.backendpreu.users.controller;
import com.backend.backendpreu.users.dto.UserProfileDTO;
import com.backend.backendpreu.users.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // 1. GET /api/users/me/profile
    @GetMapping("/users/me/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        // Asumiendo que tu UserDetails tiene el ID. Si no, búscalo por email.
        // Aquí simplifico asumiendo que puedes extraer el ID del principal.
        // Dependerá de cómo tengas configurado tu JWT.
        Long currentUserId = extractIdFromAuth(authentication);

        return ResponseEntity.ok(userProfileService.getUserProfile(currentUserId));
    }

    // 2. GET /api/admin/users/{userId}/profile
    @GetMapping("/admin/users/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')") // Solo admins pueden ver perfiles ajenos
    public ResponseEntity<UserProfileDTO> getUserProfileByAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    private Long extractIdFromAuth(Authentication auth) {
        // Ejemplo: Si tu principal es un objeto User custom
        // return ((User) auth.getPrincipal()).getId();
        // O buscar por email:
        // return userRepository.findByEmail(auth.getName()).get().getId();
        return 1L; // Placeholder temporal
    }
}
