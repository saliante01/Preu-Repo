package com.backend.backendpreu.auth.controller;

import com.backend.backendpreu.auth.dto.AuthResponseDTO;
import com.backend.backendpreu.auth.dto.LoginRequestDTO;
import com.backend.backendpreu.auth.dto.UserSummaryDTO;
import com.backend.backendpreu.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    private static final long COOKIE_EXPIRY = 24 * 60 * 60;

    @PostMapping("/login")
    public ResponseEntity<UserSummaryDTO> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        AuthResponseDTO authResult = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("accessCookie", authResult.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(COOKIE_EXPIRY)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        UserSummaryDTO userSummary = UserSummaryDTO.builder()
                .id(authResult.getUserId())
                .email(authResult.getEmail())
                .firstName(authResult.getFullName().split(" ")[0])
                .lastName(authResult.getFullName().split(" ")[1])
                .role(authResult.getRole().name())
                .active(true)
                .build();

        return ResponseEntity.ok(userSummary);
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDTO> getAuthenticatedUser() {
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }
}