package com.backend.backendpreu.academicPeriod.model;
import com.backend.backendpreu.courses.model.Course;
import com.backend.backendpreu.meetings.model.Meeting;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

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
     * Curso base asociado (ej: Matemática)
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * Fecha de inicio del período académico
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Fecha de término del período académico
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Estado del período (ACTIVE / CLOSED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcademicPeriodStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @OneToMany(mappedBy = "academicPeriod")
    private List<Meeting> meetings;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
