package com.backend.backendpreu.meetings.model;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "meetings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Período académico al que pertenece la sesión
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    /**
     * Título o descripción de la sesión
     * Ej: "Clase 3 – Funciones cuadráticas"
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Fecha y hora programada de la sesión
     */
    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    /**
     * Enlace a Google Meet
     */
    @Column(name = "google_meet_link", length = 500)
    private String googleMeetLink;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
