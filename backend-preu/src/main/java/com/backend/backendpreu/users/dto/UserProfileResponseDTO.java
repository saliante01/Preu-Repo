package com.backend.backendpreu.users.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserProfileResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Boolean active;

    // El historial académico
    private List<AcademicHistoryDTO> academicHistory;
}
