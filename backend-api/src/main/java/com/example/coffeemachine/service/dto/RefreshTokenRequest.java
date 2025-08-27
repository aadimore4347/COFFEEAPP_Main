package com.example.coffeemachine.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for refresh token requests.
 */
@Data
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}