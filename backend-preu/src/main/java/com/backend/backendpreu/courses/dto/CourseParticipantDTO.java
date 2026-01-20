package com.backend.backendpreu.courses.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseParticipantDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String role; // Devolverá "MAIN_PROFESSOR" o "STUDENT"
}
