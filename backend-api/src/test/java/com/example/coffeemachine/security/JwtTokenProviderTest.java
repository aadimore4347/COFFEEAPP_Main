package com.example.coffeemachine.security;

import com.example.coffeemachine.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 86400000L);

        userPrincipal = UserPrincipal.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .authorities(Arrays.asList())
                .build();
    }

    @Test
    void generateToken_ValidUser_ReturnsToken() {
        String token = jwtTokenProvider.generateToken(userPrincipal);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserIdFromJWT_ValidToken_ReturnsUserId() {
        String token = jwtTokenProvider.generateToken(userPrincipal);
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);
        
        assertEquals(1L, userId);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtTokenProvider.generateToken(userPrincipal);
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Set a very short expiration time
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1L);
        
        String token = jwtTokenProvider.generateToken(userPrincipal);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean isValid = jwtTokenProvider.validateToken(token);
        assertFalse(isValid);
    }
}