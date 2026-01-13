package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.users.dto.UserResponseDTO;
import com.backend.backendpreu.users.dto.CreateUserRequestDTO;
import com.backend.backendpreu.users.dto.UpdateUserRequestDTO;
import com.backend.backendpreu.users.dto.ChangeRoleRequestDTO;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.backend.backendpreu.users.model.Role;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody CreateUserRequestDTO request,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO newUser = userService.createUser(request, adminUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequestDTO request,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO updatedUser = userService.updateUser(userId, request, adminUser);
        return ResponseEntity.ok(updatedUser);
    }
    @PatchMapping("/{userId}/desactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> desactiveUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO user = userService.desactivateUser(userId, adminUser);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> activateUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO user = userService.activateUser(userId, adminUser);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> changeRole(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeRoleRequestDTO request,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO user = userService.changeUserRole(userId, request, adminUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserResponseDTO> users = userService.getAllUsers(role, active, pageable);
        return ResponseEntity.ok(users);
    }
}