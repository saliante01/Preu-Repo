package com.backend.backendpreu.courses.Repository;
import com.backend.backendpreu.courses.model.SchoolTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolTermRepository extends JpaRepository<SchoolTerm, Long> {

    // Buscar el semestre activo actualmente (ej: para inscribir alumnos)
    Optional<SchoolTerm> findByActiveTrue();

    // Buscar por nombre (ej: "2025-1")
    Optional<SchoolTerm> findByName(String name);
}
