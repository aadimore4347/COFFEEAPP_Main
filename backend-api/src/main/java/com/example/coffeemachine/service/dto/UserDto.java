package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user information (excluding sensitive data like password).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    private String username;
    private UserRole role;
    private FacilityDto facility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}