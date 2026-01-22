package com.backend.backendpreu.academicPeriod.repository;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link AcademicPeriod} entities.
 */
@Repository
public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    /**
     * Finds all academic periods associated with a specific school term.
     *
     * @param schoolTermId The ID of the school term.
     * @return A list of {@link AcademicPeriod} entities.
     */
    List<AcademicPeriod> findBySchoolTermId(Long schoolTermId);

    /**
     * Checks if any academic period exists for a given school term ID.
     *
     * @param termId The ID of the school term.
     * @return true if academic periods exist, false otherwise.
     */
    boolean existsBySchoolTermId(Long termId);

    /**
     * Finds all academic periods with a specific status.
     *
     * @param status The {@link AcademicPeriodStatus} to filter by.
     * @return A list of {@link AcademicPeriod} entities.
     */
    List<AcademicPeriod> findByStatus(AcademicPeriodStatus status);

    /**
     * Finds an academic period by its ID, eagerly fetching associated course and school term details.
     *
     * @param id The ID of the academic period.
     * @return An {@link Optional} containing the {@link AcademicPeriod} with details if found, otherwise empty.
     */
    @Query("SELECT ap FROM AcademicPeriod ap " +
            "JOIN FETCH ap.course " +
            "JOIN FETCH ap.schoolTerm " +
            "WHERE ap.id = :id")
    Optional<AcademicPeriod> findByIdWithDetails(@Param("id") Long id);


    /**
     * Finds academic periods that are currently active, belong to an active school term,
     * and the specified student is not yet enrolled in.
     *
     * @param studentId The ID of the student.
     * @return A list of {@link AcademicPeriod} entities available for enrollment.
     */
    @Query("SELECT ap FROM AcademicPeriod ap " +
            "WHERE ap.schoolTerm.active = true " +
            "AND ap.status = 'ACTIVE' " +
            "AND ap.id NOT IN (" +
            "    SELECT cp.academicPeriod.id " +
            "    FROM CourseParticipation cp " +
            "    WHERE cp.user.id = :studentId" +
            ")")
    List<AcademicPeriod> findAvailableForStudent(@Param("studentId") Long studentId);

    /**
     * Checks if any academic period exists for a given course ID.
     *
     * @param courseId The ID of the course.
     * @return true if academic periods exist for the course, false otherwise.
     */
    boolean existsByCourseId(Long courseId);

    /**
     * Finds all academic periods associated with a specific course ID.
     *
     * @param courseId The ID of the course.
     * @return A list of {@link AcademicPeriod} entities.
     */
    List<AcademicPeriod> findByCourseId(Long courseId);

    /**
     * Checks for the existence of an academic period with the same course, school term, and class schedule.
     * This is used to prevent duplicate class offerings.
     *
     * @param courseId The ID of the course.
     * @param schoolTermId The ID of the school term.
     * @param dayOfWeek The day of the week for the schedule.
     * @param startTime The start time for the schedule.
     * @param endTime The end time for the schedule.
     * @return true if a duplicate academic period with the same schedule exists, false otherwise.
     */
    boolean existsByCourseIdAndSchoolTermIdAndSchedule_DayOfWeekAndSchedule_StartTimeAndSchedule_EndTime(
            Long courseId, Long schoolTermId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}
