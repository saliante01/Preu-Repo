package com.backend.backendpreu.auth.service;

import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.auth.dto.AuthResponseDTO;
import com.backend.backendpreu.auth.dto.LoginRequestDTO;
import com.backend.backendpreu.auth.dto.UserSummaryDTO;
import com.backend.backendpreu.auth.security.JwtService;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for handling user authentication operations.
 * Manages user login, JWT generation, and retrieval of authenticated user details.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final CaptchaService captchaService;

    /**
     * Authenticates a user based on their email, password, and a reCAPTCHA token.
     * On successful authentication, a JWT is generated and an audit log is recorded.
     *
     * @param request The {@link LoginRequestDTO} containing the user's login credentials and captcha token.
     * @return An {@link AuthResponseDTO} containing the user's ID, email, full name, role, and the generated JWT.
     * @throws RuntimeException If the captcha is invalid or expired.
     * @throws ResponseStatusException If the email or password is incorrect, or if the user account is inactive.
     */
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request){

        boolean isHuman = captchaService.verify(request.getCaptchaToken());
        if (!isHuman) {
            throw new RuntimeException("Invalid or expired captcha. Are you a robot?");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!user.getActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is deactivated. Contact administration."
            );
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        String token = jwtService.generateToken(user);

        auditLogService.log(
                user,
                "LOGIN",
                "USER",
                user.getId(),
                "Successful login via Email/Password"

        );
        return AuthResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .token(token)
                .build();
    }

    /**
     * Retrieves a summary of the currently authenticated user from the SecurityContext.
     *
     * @return A {@link UserSummaryDTO} containing basic details of the authenticated user.
     * @throws ResponseStatusException If no user is authenticated or the authenticated user is not found in the repository.
     */
    @Transactional(readOnly = true)
    public UserSummaryDTO getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        return UserSummaryDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .build();
    }

}
