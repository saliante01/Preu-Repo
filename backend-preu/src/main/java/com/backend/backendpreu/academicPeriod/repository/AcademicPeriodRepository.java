package com.backend.backendpreu.academicPeriod.repository;

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

    List<AcademicPeriod> findBySchoolTermId(Long schoolTermId);

    boolean existsBySchoolTermId(Long termId);

    List<AcademicPeriod> findByStatus(AcademicPeriodStatus status);

    @Query("SELECT ap FROM AcademicPeriod ap " +
            "JOIN FETCH ap.course " +
            "JOIN FETCH ap.schoolTerm " +
            "WHERE ap.id = :id")
    Optional<AcademicPeriod> findByIdWithDetails(@Param("id") Long id);


    @Query("SELECT ap FROM AcademicPeriod ap " +
            "WHERE ap.schoolTerm.active = true " +
            "AND ap.status = 'ACTIVE' " +
            "AND ap.id NOT IN (" +
            "    SELECT cp.academicPeriod.id " +
            "    FROM CourseParticipation cp " +
            "    WHERE cp.user.id = :studentId" +
            ")")
    List<AcademicPeriod> findAvailableForStudent(@Param("studentId") Long studentId);
}