package com.backend.backendpreu.users.repository;

import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link User}.
 *
 * Responsabilidades:
 * - Acceso a datos de usuarios
 * - Búsqueda por email
 * - Verificación de existencia por email
 * - Obtención de usuarios con filtros dinámicos
 * - Obtención de usuarios por rol
 *
 * Este repositorio es utilizado principalmente por:
 * - Servicios de autenticación
 * - Servicios administrativos de usuarios
 * - Servicios de perfil de usuario
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email.
     *
     * Se utiliza principalmente durante:
     * - Autenticación
     * - Resolución del usuario actual
     * - Validaciones de existencia
     *
     * @param email Email único del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email indicado.
     *
     * Usado principalmente para:
     * - Validar duplicados al crear usuarios
     *
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene un listado paginado de usuarios aplicando filtros opcionales.
     *
     * Si un filtro es null:
     * - No se aplica en la búsqueda
     *
     * Permite:
     * - Filtrar por rol
     * - Filtrar por estado activo/inactivo
     *
     * @param role Rol del usuario (opcional)
     * @param active Estado activo/inactivo (opcional)
     * @param pageable Configuración de paginación
     * @return Página de usuarios filtrados
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:active IS NULL OR u.active = :active)")
    Page<User> findAllByFilters(
            @Param("role") Role role,
            @Param("active") Boolean active,
            Pageable pageable
    );

    /**
     * Obtiene todos los usuarios que tengan un rol específico.
     *
     * Usado principalmente para:
     * - Operaciones masivas por tipo de usuario
     * - Dashboards administrativos
     * - Listados de profesores, alumnos, etc.
     *
     * @param role Rol a buscar
     * @return Lista de usuarios con ese rol
     */
    List<User> findByRole(Role role);
}
