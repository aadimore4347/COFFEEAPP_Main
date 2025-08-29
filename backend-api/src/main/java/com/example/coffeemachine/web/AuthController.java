package com.example.coffeemachine.web;

import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import com.example.coffeemachine.repository.UserRepository;
import com.example.coffeemachine.security.JwtService;
import com.example.coffeemachine.service.PasswordService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordService passwordService,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.username());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        User user = userOpt.get();
        if (!passwordService.matches(req.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        if (user.getFacility() != null) {
            claims.put("facilityId", user.getFacility().getId());
        }
        String accessToken = jwtService.generateToken(user.getUsername(), claims);

        Map<String, Object> body = new HashMap<>();
        body.put("accessToken", accessToken);
        body.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "facilityId", user.getFacility() != null ? user.getFacility().getId() : null
        ));
        return ResponseEntity.ok(body);
    }

    public record RegisterRequest(@NotBlank String username,
                                  @NotBlank String password,
                                  String role,
                                  Long facilityId) {}

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        UserRole role = (req.role() == null || req.role().isBlank()) ? UserRole.FACILITY : UserRole.valueOf(req.role());
        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(passwordService.hash(req.password()));
        user.setRole(role);
        if (role == UserRole.FACILITY && req.facilityId() != null) {
            Facility facility = new Facility();
            facility.setId(req.facilityId()); // Reference by id only
            user.setFacility(facility);
        }
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    public record RefreshRequest(@NotBlank String refreshToken) {}

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
        // Simple implementation: issue a new token for demo purposes
        // In production, validate a stored refresh token
        return ResponseEntity.badRequest().body(Map.of("error", "Refresh not implemented with persistence"));
    }
}

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