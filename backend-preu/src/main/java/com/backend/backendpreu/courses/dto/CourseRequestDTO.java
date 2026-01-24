package com.backend.backendpreu.courses.dto;

import com.backend.backendpreu.courses.model.Subject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for creating or updating a Course.
 * Used to encapsulate course details received from client requests.
 */
@Data
public class CourseRequestDTO {
    /**
     * The name of the course.
     */
    private String name;
    /**
     * The unique code for the course.
     */
    private String code;
    /**
     * A description of the course.
     */
    private String description;
    /**
     * The subject of the course. Must not be null.
     */
    @NotNull(message = "La asignatura es obligatoria")
    private Subject subject;

}
