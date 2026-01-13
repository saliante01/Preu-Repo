package com.backend.backendpreu.academicPaticipation.repository;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    // Buscar todos los cursos que se están dictando en un Semestre específico
    List<AcademicPeriod> findBySchoolTermId(Long schoolTermId);

    // Buscar todos los cursos activos
    List<AcademicPeriod> findByStatus(AcademicPeriodStatus status);

    // OPTIMIZADO: Traer el detalle completo de un curso por ID
    // Carga de una sola vez el Curso base y el Semestre (SchoolTerm)
    @Query("SELECT ap FROM AcademicPeriod ap " +
            "JOIN FETCH ap.course " +
            "JOIN FETCH ap.schoolTerm " +
            "WHERE ap.id = :id")
    Optional<AcademicPeriod> findByIdWithDetails(@Param("id") Long id);
}
