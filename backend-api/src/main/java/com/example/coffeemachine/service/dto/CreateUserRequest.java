package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating new users.
 */
@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    @NotNull(message = "User role is required")
    private UserRole role;
    
    /**
     * Facility ID - required for FACILITY role users, should be null for ADMIN users.
     */
    private Long facilityId;
}