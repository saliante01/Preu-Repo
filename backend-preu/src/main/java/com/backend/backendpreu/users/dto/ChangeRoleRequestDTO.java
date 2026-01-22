package com.backend.backendpreu.users.dto;

import com.backend.backendpreu.users.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequestDTO {
    @NotNull(message = "El rol es obligatorio")
    //para enums ocupar notnull, para string notblank
    private Role role;
}
