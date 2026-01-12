package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.users.dto.UserResponseDTO;
import com.backend.backendpreu.users.dto.CreateUserRequestDTO;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody CreateUserRequestDTO request,
            @AuthenticationPrincipal User adminUser // Obtenemos al admin del token
    ) {
        UserResponseDTO newUser = userService.createUser(request, adminUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
