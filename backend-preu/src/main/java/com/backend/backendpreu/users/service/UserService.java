package com.backend.backendpreu.users.service;

import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.users.dto.ChangeRoleRequestDTO;
import com.backend.backendpreu.users.dto.CreateUserRequestDTO;
import com.backend.backendpreu.users.dto.UpdateUserRequestDTO;
import com.backend.backendpreu.users.dto.UserResponseDTO;
import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
// CORRECCIÓN 1: IMPORT CORRECTO
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    // ... (CREATE y UPDATE se mantienen igual) ...
    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request, User adminUser) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .active(true)
                .build();
        User savedUser = userRepository.save(newUser);
        String detalleAuditoria = String.format("Creación de usuario. Email: %s, Rol: %s", savedUser.getEmail(), savedUser.getRole());
        auditLogService.log(adminUser, "CREATE_USER", "USER", savedUser.getId(), detalleAuditoria);
        return mapToDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UpdateUserRequestDTO request, User adminUser) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        if (!targetUser.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email " + request.getEmail() + " ya está en uso.");
        }
        targetUser.setFirstName(request.getFirstName());
        targetUser.setLastName(request.getLastName());
        targetUser.setEmail(request.getEmail());
        User updatedUser = userRepository.save(targetUser);
        auditLogService.log(
                adminUser,
                "UPDATE_USER",
                "USER",
                targetUser.getId(),
                "Actualización de perfil: " + targetUser.getEmail()
        );
        return mapToDTO(updatedUser);
    }

    @Transactional
    public UserResponseDTO desactivateUser(Long userId, User adminUser) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        if(targetUser.getId().equals(adminUser.getId())) {
            throw new RuntimeException("No puedes desactivar tu propia cuenta de administrador");
        }
        if(!targetUser.getActive()){
            throw new RuntimeException("El usuario ya se encuentra desactivado");
        }
        targetUser.setActive(false);
        User savedUser = userRepository.save(targetUser);
        auditLogService.log(
                adminUser,
                "DEACTIVATE_USER",
                "USER",
                targetUser.getId(),
                "Usuario desactivado: " + targetUser.getEmail()
        );
        return mapToDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO activateUser(Long userId, User adminUser) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        if(targetUser.getActive()){
            throw new RuntimeException("El usuario ya se encuentra activo");
        }

        targetUser.setActive(true);
        User savedUser = userRepository.save(targetUser);

        auditLogService.log(
                adminUser,
                "ACTIVATE_USER",
                "USER",
                targetUser.getId(),
                "Usuario reactivado: " + targetUser.getEmail()
        );
        return mapToDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO changeUserRole(Long userId, ChangeRoleRequestDTO request, User adminUser) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: "+ userId));
        if(targetUser.getId().equals(adminUser.getId())) {
            throw new RuntimeException("No puedes cambiar tu propio rol");
        }
        if (targetUser.getRole() == request.getRole()) {
            throw new RuntimeException("El usuario ya tiene el rol " + request.getRole());
        }
        Role oldRole = targetUser.getRole();
        targetUser.setRole(request.getRole());
        User savedUser = userRepository.save(targetUser);

        String detalle = String.format("Cambio de Rol: De %s a %s. Usuario: %s",
                oldRole, request.getRole(), targetUser.getEmail());

        // CORRECCIÓN TYPO: Change_ROLE -> CHANGE_ROLE
        auditLogService.log(adminUser, "CHANGE_ROLE", "USER", targetUser.getId(), detalle);

        return mapToDTO(savedUser);
    }


    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Role role, Boolean active, Pageable pageable) {
        Page<User> userPage = userRepository.findAllByFilters(role, active, pageable);
        return userPage.map(this::mapToDTO);
    }

    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}