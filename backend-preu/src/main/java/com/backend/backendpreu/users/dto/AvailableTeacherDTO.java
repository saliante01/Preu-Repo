package com.backend.backendpreu.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailableTeacherDTO {
    private Long id;
    private String fullName;
    private Double currentHours;
    private String status;
}
