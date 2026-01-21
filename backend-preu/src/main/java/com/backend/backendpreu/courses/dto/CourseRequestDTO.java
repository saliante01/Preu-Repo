package com.backend.backendpreu.courses.dto;

import com.backend.backendpreu.courses.model.Subject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequestDTO {
    private String name;
    private String code;
    private String description;
    @NotNull(message = "La asignatura es obligatoria")
    private Subject subject;

}
