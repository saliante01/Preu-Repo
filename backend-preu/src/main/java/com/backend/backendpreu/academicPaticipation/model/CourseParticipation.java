package com.backend.backendpreu.academicPaticipation.model;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents the participation of a user in a specific academic period.
 *
 * <p>This entity defines how a {@link User} is linked to an
 * {@link AcademicPeriod}, including the role played and the participation
 * lifecycle.</p>
 *
 * <p>It is used to manage enrollments, teaching assignments and historical
 * participation data without relying on a simple many-to-many relationship.</p>
 *
 * <p>A user may participate in the same academic period with different roles,
 * but only once per role.</p>
 */
@Entity
@Table(
        name = "course_participation",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"academic_period_id", "user_id", "role"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Academic period in which the user participates.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    /**
     * User participating in the academic period.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Role of the user within the academic period.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseRole role;

    /**
     * Current participation status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    /**
     * Timestamp when the user was enrolled in the academic period.
     */
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private OffsetDateTime enrolledAt;

    /**
     * Timestamp when the user left the academic period, if applicable.
     */
    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = OffsetDateTime.now();
        status = ParticipationStatus.ACTIVE;
    }
}
