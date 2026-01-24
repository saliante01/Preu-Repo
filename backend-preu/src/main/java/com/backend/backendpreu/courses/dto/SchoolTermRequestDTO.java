package com.backend.backendpreu.courses.dto;
import java.time.LocalDate;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating a SchoolTerm.
 * Encapsulates the details required for school term operations.
 */
@Data
public class SchoolTermRequestDTO {
    /**
     * The name of the school term (e.g., "2025-1").
     */
    private String name;
    /**
     * The start date of the school term.
     */
    private LocalDate startDate;
    /**
     * The end date of the school term.
     */
    private LocalDate endDate;
}
