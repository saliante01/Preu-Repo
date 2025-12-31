package com.backend.backendpreu.courses.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
@Entity
@Table(
        name="courses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code"),
                @UniqueConstraint(columnNames = "name")
        }
)
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 50)
    private String code;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

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
