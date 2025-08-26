package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JWT authentication response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    
    /**
     * The JWT access token.
     */
    private String accessToken;
    
    /**
     * The JWT refresh token.
     */
    private String refreshToken;
    
    /**
     * Token type (usually "Bearer").
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * Token expiration time in seconds.
     */
    private Long expiresIn;
    
    /**
     * User information.
     */
    private UserDto user;
}