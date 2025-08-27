package com.example.coffeemachine.web;

import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.service.AlertService;
import com.example.coffeemachine.service.CoffeeMachineService;
import com.example.coffeemachine.service.FacilityService;
import com.example.coffeemachine.service.UserService;
import com.example.coffeemachine.service.dto.*;
import com.example.coffeemachine.service.mapper.AlertMapper;
import com.example.coffeemachine.service.mapper.CoffeeMachineMapper;
import com.example.coffeemachine.service.mapper.FacilityMapper;
import com.example.coffeemachine.service.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for administrative operations.
 * Handles facility management, user management, and system-wide monitoring.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration", description = "Administrative operations for system management")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FacilityService facilityService;
    private final UserService userService;
    private final CoffeeMachineService coffeeMachineService;
    private final AlertService alertService;
    private final FacilityMapper facilityMapper;
    private final UserMapper userMapper;
    private final CoffeeMachineMapper coffeeMachineMapper;
    private final AlertMapper alertMapper;

    // ================== Facility Management ==================

    /**
     * Create a new facility.
     *
     * @param createFacilityRequest the facility creation request
     * @return created facility details
     */
    @PostMapping("/facility")
    @Operation(summary = "Create facility", description = "Create a new facility")
    public ResponseEntity<ApiResponse<FacilityDto>> createFacility(@Valid @RequestBody CreateFacilityRequest createFacilityRequest) {
        log.info("Creating new facility: {}", createFacilityRequest.getName());
        
        try {
            Facility facility = facilityMapper.toEntity(createFacilityRequest);
            Facility createdFacility = facilityService.createFacility(facility);
            FacilityDto facilityDto = facilityMapper.toDto(createdFacility);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(facilityDto, "Facility created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "FACILITY_NAME_EXISTS"));
        }
    }

    /**
     * Get all facilities.
     *
     * @return list of all facilities
     */
    @GetMapping("/facilities")
    @Operation(summary = "Get all facilities", description = "Retrieve all active facilities")
    public ResponseEntity<ApiResponse<List<FacilityDto>>> getAllFacilities() {
        log.debug("Getting all facilities");
        
        List<Facility> facilities = facilityService.findAllActive();
        List<FacilityDto> facilityDtos = facilityMapper.toDto(facilities);
        
        return ResponseEntity.ok(ApiResponse.success(facilityDtos, "Facilities retrieved successfully"));
    }

    /**
     * Update an existing facility.
     *
     * @param facilityId the facility ID
     * @param updateFacilityRequest the facility update request
     * @return updated facility details
     */
    @PutMapping("/facility/{facilityId}")
    @Operation(summary = "Update facility", description = "Update an existing facility")
    public ResponseEntity<ApiResponse<FacilityDto>> updateFacility(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId,
            @Valid @RequestBody UpdateFacilityRequest updateFacilityRequest) {
        
        log.info("Updating facility: {}", facilityId);
        
        try {
            Facility updates = facilityMapper.toEntity(updateFacilityRequest);
            return facilityService.updateFacility(facilityId, updates)
                    .map(facility -> {
                        FacilityDto facilityDto = facilityMapper.toDto(facility);
                        return ResponseEntity.ok(ApiResponse.success(facilityDto, "Facility updated successfully"));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "FACILITY_NAME_EXISTS"));
        }
    }

    /**
     * Delete a facility.
     *
     * @param facilityId the facility ID
     * @return deletion confirmation
     */
    @DeleteMapping("/facility/{facilityId}")
    @Operation(summary = "Delete facility", description = "Soft delete a facility and all its machines")
    public ResponseEntity<ApiResponse<String>> deleteFacility(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        
        log.info("Deleting facility: {}", facilityId);
        
        boolean deleted = facilityService.deleteFacility(facilityId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Facility deleted successfully", "Facility and all its machines have been deactivated"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ================== User Management ==================

    /**
     * Create a new user.
     *
     * @param createUserRequest the user creation request
     * @return created user details
     */
    @PostMapping("/user")
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating new user: {} with role {}", createUserRequest.getUsername(), createUserRequest.getRole());
        
        try {
            User user = userMapper.toEntity(createUserRequest);
            
            // Set facility if provided and role is FACILITY
            if (createUserRequest.getFacilityId() != null) {
                if (!facilityService.existsById(createUserRequest.getFacilityId())) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Facility not found", "FACILITY_NOT_FOUND"));
                }
                // The facility will be set by UserService.createUser
            }
            
            User createdUser = userService.createUser(user, createUserRequest.getPassword());
            UserDto userDto = userMapper.toDto(createdUser);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userDto, "User created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "USER_CREATION_FAILED"));
        }
    }

    /**
     * Get all users.
     *
     * @return list of all users
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve all active users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        log.debug("Getting all users");
        
        List<User> users = userService.findAllActive();
        List<UserDto> userDtos = userMapper.toDto(users);
        
        return ResponseEntity.ok(ApiResponse.success(userDtos, "Users retrieved successfully"));
    }

    /**
     * Update a user.
     *
     * @param userId the user ID
     * @param updateUserRequest the user update request
     * @return updated user details
     */
    @PutMapping("/user/{userId}")
    @Operation(summary = "Update user", description = "Update an existing user")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        
        log.info("Updating user: {}", userId);
        
        try {
            User updates = userMapper.toEntity(updateUserRequest);
            return userService.updateUser(userId, updates)
                    .map(user -> {
                        UserDto userDto = userMapper.toDto(user);
                        return ResponseEntity.ok(ApiResponse.success(userDto, "User updated successfully"));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "USER_UPDATE_FAILED"));
        }
    }

    /**
     * Delete a user.
     *
     * @param userId the user ID
     * @return deletion confirmation
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete user", description = "Soft delete a user account")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        log.info("Deleting user: {}", userId);
        
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", "User account has been deactivated"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ================== System Monitoring ==================

    /**
     * Get overall system usage statistics.
     *
     * @return system usage statistics
     */
    @GetMapping("/usage")
    @Operation(summary = "Get system usage", description = "Get overall system usage statistics")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> getSystemUsage() {
        log.debug("Getting system usage statistics");
        
        // This would typically aggregate data from multiple services
        // For now, we'll return a basic implementation
        DashboardSummaryDto summary = DashboardSummaryDto.builder()
                .timestamp(java.time.LocalDateTime.now())
                .totalFacilities(facilityService.findAllActive().size())
                .totalMachines(coffeeMachineService.findAllActive().size())
                .totalUnresolvedAlerts(alertService.getAllUnresolvedAlerts().size())
                .criticalAlerts(alertService.getCriticalAlerts().size())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(summary, "System usage retrieved successfully"));
    }

    /**
     * Get all system alerts.
     *
     * @return list of all alerts
     */
    @GetMapping("/alerts")
    @Operation(summary = "Get all alerts", description = "Get all unresolved alerts across the system")
    public ResponseEntity<ApiResponse<List<AlertDto>>> getAllAlerts() {
        log.debug("Getting all system alerts");
        
        List<com.example.coffeemachine.domain.Alert> alerts = alertService.getAllUnresolvedAlerts();
        List<AlertDto> alertDtos = alertMapper.toDto(alerts);
        
        return ResponseEntity.ok(ApiResponse.success(alertDtos, "Alerts retrieved successfully"));
    }

    /**
     * Get all critical alerts.
     *
     * @return list of critical alerts
     */
    @GetMapping("/alerts/critical")
    @Operation(summary = "Get critical alerts", description = "Get all critical unresolved alerts")
    public ResponseEntity<ApiResponse<List<AlertDto>>> getCriticalAlerts() {
        log.debug("Getting critical system alerts");
        
        List<com.example.coffeemachine.domain.Alert> alerts = alertService.getCriticalAlerts();
        List<AlertDto> alertDtos = alertMapper.toDto(alerts);
        
        return ResponseEntity.ok(ApiResponse.success(alertDtos, "Critical alerts retrieved successfully"));
    }

    /**
     * Get alert statistics.
     *
     * @return alert statistics
     */
    @GetMapping("/alerts/statistics")
    @Operation(summary = "Get alert statistics", description = "Get overall alert statistics")
    public ResponseEntity<ApiResponse<AlertStatisticsDto>> getAlertStatistics() {
        log.debug("Getting alert statistics");
        
        com.example.coffeemachine.service.AlertStatistics stats = alertService.getOverallAlertStatistics();
        AlertStatisticsDto statsDto = alertMapper.toDto(stats);
        
        return ResponseEntity.ok(ApiResponse.success(statsDto, "Alert statistics retrieved successfully"));
    }
}