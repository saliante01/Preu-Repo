package com.backend.backendpreu.academicPeriod.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for summarizing academic period information.
 * Used for displaying a concise overview of an academic period
 * in lists or dashboards.
 */
@Data
@Builder
public class AcademicPeriodSummaryDTO {
    /**
     * The unique identifier of the academic period.
     */
    private Long id;
    /**
     * The name of the associated course (e.g., "Mathematics M1").
     */
    private String courseName;
    /**
     * The code of the associated course (e.g., "MAT-PAES-1").
     */
    private String courseCode;
    /**
     * The name of the school term (e.g., "Admission 2026").
     */
    private String termName;
    /**
     * A description of the course (e.g., "Intensive training").
     */
    private String description;
    /**
     * A formatted string representing the class schedule (e.g., "MONDAY 10:00 - 11:00").
     */
    private String schedule;
    /**
     * The current status of the academic period (e.g., "ACTIVE", "PENDING_CLOSURE", "FINISHED").
     */
    private String status;
    /**
     * The start date of the academic period.
     */
    private LocalDate startDate;
    /**
     * The end date of the academic period.
     */
    private LocalDate endDate;
    /**
     * The maximum capacity of students for this academic period.
     */
    private Integer maxCapacity;
    /**
     * The current number of students enrolled in this academic period.
     */
    private Integer currentEnrollment;
}
