package com.backend.backendpreu.academicPeriod.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AcademicPeriodSummaryDTO {
    private Long id;            // Este es el academicPeriodId que necesitas para el POST
    private String courseName;  // Ej: "Matemáticas M1"
    private String courseCode;  // Ej: "MAT-PAES-1"
    private String termName;    // Ej: "Admisión 2026"
    private String description; // Ej: "Entrenamiento intensivo"

    private String status;      // Para saber si es ACTIVE, PENDING o FINISHED
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxCapacity;
    private Integer currentEnrollment;
}
