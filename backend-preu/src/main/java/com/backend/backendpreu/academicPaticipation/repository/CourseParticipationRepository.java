package com.backend.backendpreu.academicPaticipation.repository;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseParticipationRepository extends JpaRepository<CourseParticipation, Long> {

    // 1. Validar si un alumno ya está inscrito en un curso específico
    boolean existsByAcademicPeriodIdAndUserId(Long academicPeriodId, Long userId);

    // 2. Traer todos los alumnos de un curso específico
    List<CourseParticipation> findByAcademicPeriodIdAndStatus(Long academicPeriodId, ParticipationStatus status);

    // 3. Buscar cursos de un usuario (para "Mis Cursos")
    List<CourseParticipation> findByUserId(Long userId);

    // 4. QUERY MAESTRA: Historial Académico Completo
    @Query("SELECT p FROM CourseParticipation p " +
            "JOIN FETCH p.academicPeriod ap " +
            "JOIN FETCH ap.course c " +
            "JOIN FETCH ap.schoolTerm st " +
            "WHERE p.user.id = :userId " +
            "ORDER BY st.startDate DESC")
    List<CourseParticipation> findFullHistoryByUserId(@Param("userId") Long userId);
}