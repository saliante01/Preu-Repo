package com.backend.backendpreu.courses.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "school_terms") // Cambio de nombre de tabla para evitar conflictos
@Data
public class SchoolTerm { // CAMBIO DE NOMBRE DE CLASE

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ej: "2025-1"

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private Boolean active;
}