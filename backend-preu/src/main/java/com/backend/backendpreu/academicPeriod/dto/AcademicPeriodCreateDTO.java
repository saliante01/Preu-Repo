package com.backend.backendpreu.academicPeriod.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private Integer maxCapacity;

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
