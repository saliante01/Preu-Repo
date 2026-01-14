package com.backend.backendpreu.courses.Repository;

import com.backend.backendpreu.courses.model.SchoolTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolTermRepository extends JpaRepository<SchoolTerm, Long> {

    // --- MÉTODOS EXISTENTES (Modificados para seguridad) ---

    // CAMBIO DE SEGURIDAD: De Optional a List
    // Si tienes "2026" y "2027" activos a la vez, Optional fallaría. List lo soporta.
    List<SchoolTerm> findByActiveTrue();

    // Buscar por nombre (ej: "2025-1")
    Optional<SchoolTerm> findByName(String name);

    // --- NUEVOS MÉTODOS (Ticket BE-AC-01) ---

    // 1. Para validar duplicados rápidamente antes de guardar
    boolean existsByName(String name);

    // 2. Para listar en el panel de Admin ordenados cronológicamente (el más nuevo primero)
    List<SchoolTerm> findAllByOrderByStartDateDesc();
    boolean existsByNameAndIdNot(String name, Long id);
}