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

/**
 * Repository interface for managing {@link CourseParticipation} entities.
 */
@Repository
public interface CourseParticipationRepository extends JpaRepository<CourseParticipation, Long> {

    /**
     * Checks if a participation record exists for a given academic period and user.
     *
     * @param academicPeriodId The ID of the academic period.
     * @param userId The ID of the user.
     * @return true if a participation exists, false otherwise.
     */
    boolean existsByAcademicPeriodIdAndUserId(Long academicPeriodId, Long userId);

    /**
     * Finds all course participations for a given academic period and status.
     *
     * @param academicPeriodId The ID of the academic period.
     * @param status The participation status to filter by.
     * @return A list of {@link CourseParticipation} entities.
     */
    List<CourseParticipation> findByAcademicPeriodIdAndStatus(Long academicPeriodId, ParticipationStatus status);

    /**
     * Finds all course participations for a given user.
     *
     * @param userId The ID of the user.
     * @return A list of {@link CourseParticipation} entities.
     */
    List<CourseParticipation> findByUserId(Long userId);

    /**
     * Finds all course participations for a given user, role, and status.
     *
     * @param userId The ID of the user.
     * @param role The role to filter by.
     * @param status The status to filter by.
     * @return A list of {@link CourseParticipation} entities.
     */
    List<CourseParticipation> findAllByUserIdAndRoleAndStatus(Long userId, CourseRole role, ParticipationStatus status);

    /**
     * Finds all course participations for a given user, a collection of roles, and a specific status.
     *
     * @param userId The ID of the user.
     * @param roles A collection of {@link CourseRole} to filter by.
     * @param status The participation status to filter by.
     * @return A list of {@link CourseParticipation} entities.
     */
    @Query("SELECT p FROM CourseParticipation p " +
            "WHERE p.user.id = :userId " +
            "AND p.role IN :roles " +
            "AND p.status = :status")
    List<CourseParticipation> findAllByUserIdAndRolesInAndStatus(
            @Param("userId") Long userId,
            @Param("roles") java.util.Collection<CourseRole> roles,
            @Param("status") ParticipationStatus status
    );

    /**
     * Counts the number of participations for a given academic period and role.
     *
     * @param academicPeriodId The ID of the academic period.
     * @param role The role to count.
     * @return The count of participations.
     */
    Integer countByAcademicPeriodIdAndRole(Long academicPeriodId, CourseRole role);

    /**
     * Retrieves the full academic history for a given user, including associated academic period, course, and school term details.
     * The results are ordered by the school term's start date in descending order.
     *
     * @param userId The ID of the user.
     * @return A list of {@link CourseParticipation} entities with eagerly fetched related data.
     */
    @Query("SELECT p FROM CourseParticipation p " +
            "JOIN FETCH p.academicPeriod ap " +
            "JOIN FETCH ap.course c " +
            "JOIN FETCH ap.schoolTerm st " +
            "WHERE p.user.id = :userId " +
            "ORDER BY st.startDate DESC")
    List<CourseParticipation> findFullHistoryByUserId(@Param("userId") Long userId);

    /**
     * Finds a specific course participation record by academic period ID and user ID.
     *
     * @param academicPeriodId The ID of the academic period.
     * @param userId The ID of the user.
     * @return An {@link Optional} containing the {@link CourseParticipation} if found, otherwise empty.
     */
    Optional<CourseParticipation> findByAcademicPeriodIdAndUserId(Long academicPeriodId, Long userId);
}
