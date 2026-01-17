package com.backend.backendpreu.users.dto;

import lombok.Data;

import java.util.Set;
@Data
public class TeacherSubjectsDTO {
    private Set<String> subjects; // EJ: ["Matemáticas,"Física"]
}
