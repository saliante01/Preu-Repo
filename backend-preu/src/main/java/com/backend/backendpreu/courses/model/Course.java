package com.backend.backendpreu.courses.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents a base course offered by the institution.
 * <p>
 * A course defines the subject itself and can be reused
 * across multiple academic periods.
 */
@Entity
@Table(
        name = "courses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code"),
                @UniqueConstraint(columnNames = "name")
        }
)
@Data
public class Course {

    /**
     * Unique identifier of the course.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Internal course code.
     */
    @Column(nullable = false, length = 50)
    private String code;

    /**
     * Course name.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * General description of the course.
     */
    @Column(length = 500)
    private String description;

    /**
     * Timestamp indicating when the course was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp indicating the last update of the course.
     */
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
