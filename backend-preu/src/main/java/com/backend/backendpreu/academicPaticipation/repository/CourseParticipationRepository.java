package com.backend.backendpreu.academicPaticipation.repository;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseParticipationRepository extends JpaRepository<CourseParticipation, Long> {

    boolean existsByAcademicPeriodIdAndUserId(Long academicPeriodId, Long userId);

    List<CourseParticipation> findByAcademicPeriodIdAndStatus(Long academicPeriodId, ParticipationStatus status);

    List<CourseParticipation> findByUserId(Long userId);

    List<CourseParticipation> findAllByUserIdAndRoleAndStatus(Long userId, CourseRole role, ParticipationStatus status);

    @Query("SELECT p FROM CourseParticipation p " +
            "WHERE p.user.id = :userId " +
            "AND p.role IN :roles " +
            "AND p.status = :status")
    List<CourseParticipation> findAllByUserIdAndRolesInAndStatus(
            @Param("userId") Long userId,
            @Param("roles") java.util.Collection<CourseRole> roles,
            @Param("status") ParticipationStatus status
    );

    Integer countByAcademicPeriodIdAndRole(Long academicPeriodId, CourseRole role);

    @Query("SELECT p FROM CourseParticipation p " +
            "JOIN FETCH p.academicPeriod ap " +
            "JOIN FETCH ap.course c " +
            "JOIN FETCH ap.schoolTerm st " +
            "WHERE p.user.id = :userId " +
            "ORDER BY st.startDate DESC")
    List<CourseParticipation> findFullHistoryByUserId(@Param("userId") Long userId);

    Optional<CourseParticipation> findByAcademicPeriodIdAndUserId(Long academicPeriodId, Long userId);
}
