package com.example.coffeemachine.web;

import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import com.example.coffeemachine.repository.FacilityRepository;
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
    private final FacilityRepository facilityRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          FacilityRepository facilityRepository, PasswordService passwordService,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.facilityRepository = facilityRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepository.findActiveByUsername(req.username());
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
        if (userRepository.findActiveByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        UserRole role = (req.role() == null || req.role().isBlank()) ? UserRole.FACILITY : UserRole.valueOf(req.role());
        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(passwordService.hash(req.password()));
        user.setRole(role);

        if (role == UserRole.FACILITY) {
            if (req.facilityId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Facility ID is required for facility users"));
            }
            Optional<Facility> facilityOpt = facilityRepository.findById(req.facilityId());
            if (facilityOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Facility not found"));
            }
            user.setFacility(facilityOpt.get());
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