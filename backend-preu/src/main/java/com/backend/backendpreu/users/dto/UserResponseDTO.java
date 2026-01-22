package com.backend.backendpreu.users.dto;
import com.backend.backendpreu.users.model.Role;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Boolean active;
    private OffsetDateTime createdAt;
}
