package com.backend.backendpreu.users.dto;

import com.backend.backendpreu.courses.model.Subject;
import lombok.Data;

import java.util.Set;
@Data
public class TeacherSubjectsDTO {
    private Set<Subject> subjects; // EJ: ["Matemáticas,"Física"]
}
