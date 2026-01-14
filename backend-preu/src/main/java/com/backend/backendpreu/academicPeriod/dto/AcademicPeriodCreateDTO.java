package com.backend.backendpreu.academicPeriod.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AcademicPeriodCreateDTO {

    // ID del Catálogo (Ej: Matemáticas M1)
    private Long courseId;

    // ID del Semestre (Ej: Verano 2026)
    private Long termId;

    // Fechas específicas de esta "clase"
    // (Pueden ser distintas a las del semestre general si el curso es más corto)
    private LocalDate startDate;
    private LocalDate endDate;
}
