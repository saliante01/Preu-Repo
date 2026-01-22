package com.backend.backendpreu.academicPeriod.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating a new academic period.
 * Contains all necessary information to define a course offering for a specific term,
 * including its schedule and capacity.
 */
@Data
public class AcademicPeriodCreateDTO {

    /**
     * The ID of the base course from the catalog (e.g., Mathematics M1).
     */
    private Long courseId;

    /**
     * The ID of the school term (e.g., Summer 2026).
     */
    private Long termId;

    /**
     * The specific start date for this academic period.
     * This might differ from the general term dates if the course is shorter.
     */
    private LocalDate startDate;
    /**
     * The specific end date for this academic period.
     * This might differ from the general term dates if the course is shorter.
     */
    private LocalDate endDate;
    /**
     * The maximum number of students allowed in this academic period.
     */
    private Integer maxCapacity;

    /**
     * The day of the week for the class schedule.
     */
    private DayOfWeek dayOfWeek;
    /**
     * The start time for the class schedule.
     */
    private LocalTime startTime;
    /**
     * The end time for the class schedule.
     */
    private LocalTime endTime;
}
