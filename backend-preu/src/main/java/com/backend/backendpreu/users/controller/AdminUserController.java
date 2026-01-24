package com.backend.backendpreu.users.controller;

import com.backend.backendpreu.users.dto.*;
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

/**
 * Controlador administrativo para la gestión de usuarios del sistema.
 *
 * Responsabilidades:
 * - Crear usuarios
 * - Editar usuarios
 * - Activar / desactivar usuarios
 * - Cambiar roles
 * - Listar usuarios con filtros y paginación
 * - Asignar ramos (subjects) a profesores
 *
 * Seguridad:
 * - Todos los endpoints requieren rol ADMIN
 *
 * Ruta base:
 * /api/admin/users
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    /**
     * Servicio principal de gestión de usuarios
     */
    private final UserService userService;

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param request Datos del usuario a crear
     * @param adminUser Usuario autenticado (debe ser ADMIN)
     * @return Usuario creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody CreateUserRequestDTO request,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO newUser = userService.createUser(request, adminUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
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

    /**
     * Desactiva un usuario (borrado lógico).
     */
    @PatchMapping("/{userId}/desactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> desactiveUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO user = userService.desactivateUser(userId, adminUser);
        return ResponseEntity.ok(user);
    }

    /**
     * Reactiva un usuario previamente desactivado.
     */
    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> activateUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User adminUser
    ) {
        UserResponseDTO user = userService.activateUser(userId, adminUser);
        return ResponseEntity.ok(user);
    }

    /**
     * Cambia el rol de un usuario.
     */
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

    /**
     * Obtiene listado paginado de usuarios con filtros opcionales.
     *
     * @param role Filtro por rol
     * @param active Filtro por estado activo/inactivo
     * @param pageable Configuración de paginación
     */
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

    /**
     * Actualiza los ramos que puede impartir un profesor.
     */
    @PutMapping("/{id}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherSubjectsDTO> updateSubjects(
            @PathVariable Long id,
            @RequestBody TeacherSubjectsDTO dto
    ) {
        User updated = userService.updateTeacherSubjects(id, dto);

        TeacherSubjectsDTO response = new TeacherSubjectsDTO();
        response.setSubjects(updated.getSubjects());

        return ResponseEntity.ok(response);
    }
}
