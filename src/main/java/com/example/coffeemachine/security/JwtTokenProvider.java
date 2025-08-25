package com.example.coffeemachine.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT Token Provider - Simplified stub for Phase 5 demo.
 * TODO: Implement proper JWT after fixing API compatibility.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshTokenExpirationMs) {
        
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        log.warn("JWT Token Provider initialized in stub mode - authentication disabled");
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return "demo-token-" + username + "-" + System.currentTimeMillis();
    }

    public String generateRefreshToken(String username) {
        return "demo-refresh-" + username + "-" + System.currentTimeMillis();
    }

    public String getUsernameFromToken(String token) {
        if (token != null && token.startsWith("demo-token-")) {
            String[] parts = token.split("-");
            if (parts.length >= 3) {
                return parts[2];
            }
        }
        return "demo-user";
    }

    public boolean validateToken(String authToken) {
        return authToken != null && authToken.startsWith("demo-token-");
    }

    public boolean validateRefreshToken(String refreshToken) {
        return refreshToken != null && refreshToken.startsWith("demo-refresh-");
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public Date getExpirationDateFromToken(String token) {
        return new Date(System.currentTimeMillis() + jwtExpirationMs);
    }

    public boolean isTokenExpired(String token) {
        return false; // Never expire in demo mode
    }
}