package com.backend.backendpreu.academicPeriod.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Represents the schedule for an academic class, specifying the day of the week
 * and the start and end times.
 */
@Entity
@Table(name = "class_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSchedule {

    /**
     * Unique identifier of the class schedule.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The day of the week for the class (e.g., MONDAY, TUESDAY).
     * Uses Java's native {@link DayOfWeek} enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    /**
     * The start time of the class (e.g., 10:00).
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * The end time of the class (e.g., 11:00).
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Calculates the duration of the class in hours.
     * This field is useful for dashboards or workload calculations.
     *
     * @return The duration of the class in decimal hours (e.g., 1.5 for 90 minutes).
     */
    public double getDurationInHours() {
        if (startTime != null && endTime != null) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            return minutes / 60.0; // Decimal division (e.g., 90 / 60.0 = 1.5)
        }
        return 0.0;
    }
}
