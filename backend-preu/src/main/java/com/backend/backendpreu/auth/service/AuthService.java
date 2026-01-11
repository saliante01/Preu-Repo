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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final CaptchaService captchaService;

    public AuthResponseDTO login(LoginRequestDTO request){

        boolean isHuman = captchaService.verify(request.getCaptchaToken());
        if (!isHuman) {
            throw new RuntimeException("Captcha inválido o expirado. ¿Eres un robot?");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if(!user.getActive()){
            throw new RuntimeException("User inactive");
        }
        if(!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(user);

        auditLogService.log(
                user,
                "LOGIN",
                "USER",
                user.getId()
        );
        return AuthResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .token(token)
                .build();
    }

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
