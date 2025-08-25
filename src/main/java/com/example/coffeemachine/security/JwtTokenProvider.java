package com.example.coffeemachine.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and extraction of user information.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshTokenExpirationMs) {
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    /**
     * Generate JWT token from user authentication.
     *
     * @param authentication the authentication object
     * @return JWT access token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * Generate JWT token from username.
     *
     * @param username the username
     * @return JWT access token
     */
    public String generateTokenFromUsername(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generate refresh token from username.
     *
     * @param username the username
     * @return JWT refresh token
     */
    public String generateRefreshToken(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("tokenType", "refresh")
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract username from JWT token.
     *
     * @param token the JWT token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validate JWT token.
     *
     * @param authToken the JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Validate refresh token.
     *
     * @param refreshToken the refresh token to validate
     * @return true if valid refresh token, false otherwise
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // Check if it's actually a refresh token
            String tokenType = claims.get("tokenType", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception ex) {
            log.error("Invalid refresh token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Get token expiration time in milliseconds.
     *
     * @return expiration time in milliseconds
     */
    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    /**
     * Get refresh token expiration time in milliseconds.
     *
     * @return refresh token expiration time in milliseconds
     */
    public long getRefreshExpirationMs() {
        return refreshTokenExpirationMs;
    }

    /**
     * Extract expiration date from token.
     *
     * @param token the JWT token
     * @return expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}