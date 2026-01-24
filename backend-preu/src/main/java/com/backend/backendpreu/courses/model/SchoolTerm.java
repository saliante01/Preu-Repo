package com.backend.backendpreu.courses.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Represents an academic school term or semester.
 * Each school term has a unique name, a start and end date, and an active status.
 */
@Entity
@Table(name = "school_terms") // Table name changed to avoid conflicts
@Data
public class SchoolTerm {

    /**
     * Unique identifier for the school term.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the school term, e.g., "2025-1". Must be unique.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * The start date of the school term.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * The end date of the school term.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Indicates whether the school term is currently active.
     */
    private Boolean active;
}
