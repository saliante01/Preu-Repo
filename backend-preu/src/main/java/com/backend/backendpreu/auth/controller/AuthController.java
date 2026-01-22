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

/**
 * REST controller for user authentication and session management.
 * Handles login requests and provides information about the currently authenticated user.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Defines the expiration time for the authentication cookie in seconds (24 hours).
     */
    private static final long COOKIE_EXPIRY = 24 * 60 * 60;

    /**
     * Handles user login requests.
     * Authenticates the user based on provided credentials and captcha token.
     * On successful login, sets an HTTP-only cookie containing the JWT.
     *
     * @param request The {@link LoginRequestDTO} containing user credentials and captcha token.
     * @param response The {@link HttpServletResponse} to add the authentication cookie to.
     * @return A {@link ResponseEntity} containing a {@link UserSummaryDTO} of the authenticated user.
     */
    @PostMapping("/login")
    public ResponseEntity<UserSummaryDTO> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        AuthResponseDTO authResult = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("accessCookie", authResult.getToken())
                .httpOnly(true)
                .secure(false) // Should be true in production with HTTPS
                .path("/")
                .maxAge(COOKIE_EXPIRY)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        UserSummaryDTO userSummary = UserSummaryDTO.builder()
                .id(authResult.getUserId())
                .email(authResult.getEmail())
                .firstName(authResult.getFullName().split(" ")[0]) // Assumes full name is "FirstName LastName"
                .lastName(authResult.getFullName().split(" ")[1]) // Assumes full name is "FirstName LastName"
                .role(authResult.getRole().name())
                .active(true) // Assuming authenticated user is always active here
                .build();

        return ResponseEntity.ok(userSummary);
    }

    /**
     * Retrieves a summary of the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing a {@link UserSummaryDTO} of the authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserSummaryDTO> getAuthenticatedUser() {
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }
}
