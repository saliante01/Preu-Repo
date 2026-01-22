package com.backend.backendpreu.academicPeriod.dto;

import lombok.Data;

/**
 * DTO for assigning a teacher to an academic period.
 * Used when an administrator assigns a professor to teach a specific course offering.
 */
@Data
public class TeacherAssignmentDTO {
    /**
     * The ID of the teacher to be assigned.
     */
    private Long teacherId;
}
