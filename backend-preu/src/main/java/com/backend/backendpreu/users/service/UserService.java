package com.backend.backendpreu.users.service;

import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.users.dto.CreateUserRequestDTO;
import com.backend.backendpreu.users.dto.UserResponseDTO;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request, User adminUser) {

        // 1. Validar Email Único
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Crear la Entidad
        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .active(true) // Activo por defecto
                .build();

        // 3. Guardar en BD
        User savedUser = userRepository.save(newUser);

        // 4. Construir detalle legible para Auditoría
        String detalleAuditoria = String.format(
                "Creación de usuario. Email: %s, Nombre: %s %s, Rol: %s",
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );

        // 5. Registrar en Auditoría
        auditLogService.log(
                adminUser,           // Actor (Admin)
                "CREATE_USER",       // Acción
                "USER",              // Entidad
                savedUser.getId(),   // ID
                detalleAuditoria     // Detalle legible
        );

        // 6. Retornar DTO
        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .active(savedUser.getActive())
                // Si tu User usa OffsetDateTime, úsalo directo. Si usa LocalDateTime, agrega .toLocalDateTime() si es necesario
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}