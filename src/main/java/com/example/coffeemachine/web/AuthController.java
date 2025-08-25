package com.example.coffeemachine.web;

import com.example.coffeemachine.service.AuthenticationService;
import com.example.coffeemachine.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles user login, token refresh, and user profile retrieval.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticate user and return JWT tokens.
     *
     * @param loginRequest the login request
     * @return JWT authentication response
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT access and refresh tokens")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for user: {}", loginRequest.getUsername());
        
        try {
            JwtAuthenticationResponse response = authenticationService.authenticateUser(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(response, "Authentication successful"));
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid username or password", "AUTH_FAILED"));
        }
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshTokenRequest the refresh token request
     * @return new JWT authentication response
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.debug("Token refresh request");
        
        try {
            JwtAuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);
            return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid refresh token", "REFRESH_FAILED"));
        }
    }

    /**
     * Get current authenticated user profile.
     *
     * @return current user information
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user profile information")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        try {
            UserDto currentUser = authenticationService.getCurrentUser();
            return ResponseEntity.ok(ApiResponse.success(currentUser, "User profile retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get current user: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not found", "USER_NOT_FOUND"));
        }
    }

    /**
     * Health check endpoint for authentication service.
     *
     * @return health status
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication health check", description = "Check if authentication service is operational")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("OK", "Authentication service is operational"));
    }
}