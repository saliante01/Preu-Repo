package com.backend.backendpreu.users.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private Long id;
    private String email;
    private String fullName; // O name + lastname
    private String rut;      // Si aplica
    private List<AcademicHistoryDTO> academicHistory;
}
