package com.backend.backendpreu.courses.dto;

import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import lombok.Data;

@Data
public class EnrollmentRequestDTO {
    private Long userId;
    private Long academicPeriodId;
    private CourseRole role;
    private boolean forceEnroll;
}
