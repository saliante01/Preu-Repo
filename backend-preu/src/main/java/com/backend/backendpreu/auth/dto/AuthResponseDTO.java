package com.backend.backendpreu.auth.dto;
import com.backend.backendpreu.users.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private Long userId;
    private String token;
    private String email;
    private String fullName;
    private Role role;

}
