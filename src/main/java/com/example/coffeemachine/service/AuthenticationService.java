package com.example.coffeemachine.service;

import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.security.JwtTokenProvider;
import com.example.coffeemachine.security.UserPrincipal;
import com.example.coffeemachine.service.dto.JwtAuthenticationResponse;
import com.example.coffeemachine.service.dto.LoginRequest;
import com.example.coffeemachine.service.dto.RefreshTokenRequest;
import com.example.coffeemachine.service.dto.UserDto;
import com.example.coffeemachine.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication operations.
 * Manages login, token generation, and token refresh functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Authenticate user and generate JWT tokens.
     *
     * @param loginRequest the login request containing username and password
     * @return JWT authentication response with tokens and user info
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT tokens
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(
            ((UserPrincipal) authentication.getPrincipal()).getUsername()
        );

        // Get user details
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        UserDto userDto = userMapper.toDto(user);

        log.info("User {} authenticated successfully", loginRequest.getUsername());

        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000) // Convert to seconds
                .user(userDto)
                .build();
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshTokenRequest the refresh token request
     * @return new JWT authentication response with fresh tokens
     * @throws RuntimeException if refresh token is invalid
     */
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        
        log.debug("Refreshing token for user");

        // Validate refresh token
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Extract username from refresh token
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        
        // Verify user still exists and is active
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new access token
        String newAccessToken = tokenProvider.generateTokenFromUsername(username);
        
        // Generate new refresh token for security
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        UserDto userDto = userMapper.toDto(user);

        log.info("Token refreshed successfully for user: {}", username);

        return JwtAuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(userDto)
                .build();
    }

    /**
     * Get current authenticated user.
     *
     * @return UserDto of the current authenticated user
     * @throws RuntimeException if no user is authenticated
     */
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("No authenticated user found");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        User user = userService.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

        return userMapper.toDto(user);
    }

    /**
     * Get current authenticated user's principal.
     *
     * @return UserPrincipal of the current authenticated user
     * @throws RuntimeException if no user is authenticated
     */
    public UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("No authenticated user found");
        }

        return (UserPrincipal) authentication.getPrincipal();
    }

    /**
     * Check if current user is admin.
     *
     * @return true if current user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        try {
            UserPrincipal userPrincipal = getCurrentUserPrincipal();
            return userPrincipal.isAdmin();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is facility user.
     *
     * @return true if current user is facility user, false otherwise
     */
    public boolean isCurrentUserFacilityUser() {
        try {
            UserPrincipal userPrincipal = getCurrentUserPrincipal();
            return userPrincipal.isFacilityUser();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current user's facility ID (for facility users).
     *
     * @return facility ID or null if user is not a facility user
     */
    public Long getCurrentUserFacilityId() {
        try {
            UserPrincipal userPrincipal = getCurrentUserPrincipal();
            return userPrincipal.getFacilityId();
        } catch (Exception e) {
            return null;
        }
    }
}