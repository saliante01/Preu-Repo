package com.backend.backendpreu.academicPeriod.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "class_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LUNES, MARTES, MIERCOLES... (Usamos el Enum nativo de Java)
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    // Hora inicio (ej: 10:00)
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Hora fin (ej: 11:00)
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Campo calculado para saber duración en horas (útil para el Dashboard)
    public double getDurationInHours() {
        if (startTime != null && endTime != null) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            return minutes / 60.0; // División decimal (Ej: 90 / 60.0 = 1.5)
        }
        return 0.0;
    }
}