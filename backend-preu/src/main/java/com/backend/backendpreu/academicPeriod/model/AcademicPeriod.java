package com.backend.backendpreu.academicPeriod.model;

import com.backend.backendpreu.courses.model.Course;
import com.backend.backendpreu.courses.model.SchoolTerm;
import com.backend.backendpreu.meetings.model.Meeting;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a concrete execution of a course within a defined time range.
 *
 * <p>An {@code AcademicPeriod} defines when a {@link Course} is offered,
 * its current lifecycle state, and serves as the central aggregation point
 * for all academic activities.</p>
 *
 * <p>All operational entities such as meetings, evaluations, content,
 * enrollments and payments are associated with an academic period.</p>
 */
@Entity
@Table(name = "academic_periods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The maximum number of students allowed in this academic period.
     */
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    /**
     * Base course associated with this academic period.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * School term to which this academic period belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private SchoolTerm schoolTerm;
    /**
     * Start date of the academic period.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * End date of the academic period.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Current lifecycle status of the academic period.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcademicPeriodStatus status;

    /**
     * Timestamp when the academic period was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * The class schedule for this academic period.
     * CascadeType.ALL ensures that if an AcademicPeriod is deleted, its schedule is also deleted.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private ClassSchedule schedule;
    /**
     * Timestamp of the last update.
     */
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Academic sessions (classes, reinforcement sessions) associated
     * with this academic period.
     */
    @OneToMany(mappedBy = "academicPeriod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meeting> meetings;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
