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
     * Internal course code. Must be unique.
     */
    @Column(nullable = false, length = 50)
    private String code;

    /**
     * Course name. Must be unique.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * General description of the course.
     */
    @Column(length = 500)
    private String description;
    /**
     * The subject category of the course.
     */
    @Enumerated(EnumType.STRING)
    @Column(name= "subject",nullable = false)
    private Subject subject;
    /**
     * Timestamp indicating when the course was created. Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp indicating the last update of the course. Automatically updated on modification.
     */
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Lifecycle callback method executed before the entity is persisted.
     * Sets the {@code createdAt} and {@code updatedAt} timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Lifecycle callback method executed before the entity is updated.
     * Updates the {@code updatedAt} timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
