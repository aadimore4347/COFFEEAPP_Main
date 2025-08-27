package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.UserRole;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for updating user information.
 */
@Data
public class UpdateUserRequest {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    private UserRole role;
    
    /**
     * Facility ID - required for FACILITY role users, should be null for ADMIN users.
     */
    private Long facilityId;
}