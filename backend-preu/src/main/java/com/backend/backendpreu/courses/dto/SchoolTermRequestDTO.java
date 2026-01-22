package com.backend.backendpreu.courses.dto;
import java.time.LocalDate;
import lombok.Data;

import java.time.LocalDate;
@Data
public class SchoolTermRequestDTO {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
