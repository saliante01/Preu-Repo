package com.backend.backendpreu.users.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AcademicHistoryDTO {
    private String courseName;      // Ej: "Matemáticas I"
    private String termName;        // Ej: "Verano 2026"
    private String role;            // Ej: "STUDENT"
    private String status;          // Ej: "ACTIVE"
    private LocalDate startDate;    // Para ordenar visualmente
}
