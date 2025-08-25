package com.example.coffeemachine.web;

import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.security.UserPrincipal;
import com.example.coffeemachine.service.AlertService;
import com.example.coffeemachine.service.AuthenticationService;
import com.example.coffeemachine.service.CoffeeMachineService;
import com.example.coffeemachine.service.FacilityService;
import com.example.coffeemachine.service.dto.*;
import com.example.coffeemachine.service.mapper.AlertMapper;
import com.example.coffeemachine.service.mapper.CoffeeMachineMapper;
import com.example.coffeemachine.service.mapper.FacilityMapper;
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
 * REST controller for facility operations.
 * Handles facility management and machine operations for facility users.
 */
@RestController
@RequestMapping("/api/facility")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Facility Management", description = "Operations for facility users and administrators")
public class FacilityController {

    private final FacilityService facilityService;
    private final CoffeeMachineService coffeeMachineService;
    private final AlertService alertService;
    private final AuthenticationService authenticationService;
    private final FacilityMapper facilityMapper;
    private final CoffeeMachineMapper coffeeMachineMapper;
    private final AlertMapper alertMapper;

    /**
     * Get facility details by ID.
     * Facility users can only access their own facility, admins can access any.
     *
     * @param facilityId the facility ID
     * @return facility details
     */
    @GetMapping("/{facilityId}")
    @Operation(summary = "Get facility details", description = "Get facility information by ID")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('FACILITY') and @authenticationService.getCurrentUserFacilityId() == #facilityId)")
    public ResponseEntity<ApiResponse<FacilityDto>> getFacility(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        
        log.debug("Getting facility details for ID: {}", facilityId);
        
        return facilityService.findById(facilityId)
                .map(facility -> {
                    FacilityDto facilityDto = facilityMapper.toDto(facility);
                    return ResponseEntity.ok(ApiResponse.success(facilityDto, "Facility retrieved successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all coffee machines in a facility.
     * Facility users can only access their own facility's machines.
     *
     * @param facilityId the facility ID
     * @return list of coffee machines
     */
    @GetMapping("/{facilityId}/machines")
    @Operation(summary = "Get facility machines", description = "Get all coffee machines in a facility")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('FACILITY') and @authenticationService.getCurrentUserFacilityId() == #facilityId)")
    public ResponseEntity<ApiResponse<List<CoffeeMachineDto>>> getFacilityMachines(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        
        log.debug("Getting machines for facility: {}", facilityId);
        
        List<CoffeeMachine> machines = coffeeMachineService.findByFacilityId(facilityId);
        List<CoffeeMachineDto> machineDtos = coffeeMachineMapper.toDto(machines);
        
        return ResponseEntity.ok(ApiResponse.success(machineDtos, "Machines retrieved successfully"));
    }

    /**
     * Add a new coffee machine to a facility.
     * Only admins can add machines to facilities.
     *
     * @param facilityId the facility ID
     * @param createMachineRequest the machine creation request
     * @return created machine details
     */
    @PostMapping("/{facilityId}/machine")
    @Operation(summary = "Add machine to facility", description = "Add a new coffee machine to a facility")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CoffeeMachineDto>> addMachineToFacility(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId,
            @Valid @RequestBody CreateMachineRequest createMachineRequest) {
        
        log.info("Adding new machine to facility: {}", facilityId);
        
        try {
            CoffeeMachine machine = coffeeMachineMapper.toEntity(createMachineRequest);
            CoffeeMachine createdMachine = facilityService.addMachineToFacility(facilityId, machine);
            CoffeeMachineDto machineDto = coffeeMachineMapper.toDto(createdMachine);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(machineDto, "Machine added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "FACILITY_NOT_FOUND"));
        }
    }

    /**
     * Get facility statistics.
     * Facility users can only access their own facility's statistics.
     *
     * @param facilityId the facility ID
     * @return facility statistics
     */
    @GetMapping("/{facilityId}/statistics")
    @Operation(summary = "Get facility statistics", description = "Get machine counts and status statistics for a facility")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('FACILITY') and @authenticationService.getCurrentUserFacilityId() == #facilityId)")
    public ResponseEntity<ApiResponse<FacilityStatisticsDto>> getFacilityStatistics(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        
        log.debug("Getting statistics for facility: {}", facilityId);
        
        return facilityService.getFacilityStatistics(facilityId)
                .map(stats -> {
                    FacilityStatisticsDto statsDto = facilityMapper.toDto(stats);
                    return ResponseEntity.ok(ApiResponse.success(statsDto, "Statistics retrieved successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get alerts for a facility.
     * Facility users can only access their own facility's alerts.
     *
     * @param facilityId the facility ID
     * @return list of alerts
     */
    @GetMapping("/{facilityId}/alerts")
    @Operation(summary = "Get facility alerts", description = "Get all unresolved alerts for machines in a facility")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('FACILITY') and @authenticationService.getCurrentUserFacilityId() == #facilityId)")
    public ResponseEntity<ApiResponse<List<AlertDto>>> getFacilityAlerts(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        
        log.debug("Getting alerts for facility: {}", facilityId);
        
        List<com.example.coffeemachine.domain.Alert> alerts = alertService.getUnresolvedAlertsByFacilityId(facilityId);
        List<AlertDto> alertDtos = alertMapper.toDto(alerts);
        
        return ResponseEntity.ok(ApiResponse.success(alertDtos, "Alerts retrieved successfully"));
    }

    /**
     * Get critical alerts for a facility.
     * Facility users can only access their own facility's alerts.
     *
     * @param facilityId the facility ID
     * @return list of critical alerts
     */
    @GetMapping("/{facilityId}/alerts/critical")
    @Operation(summary = "Get critical facility alerts", description = "Get critical unresolved alerts for machines in a facility")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('FACILITY') and @authenticationService.getCurrentUserFacilityId() == #facilityId)")
    public ResponseEntity<ApiResponse<List<AlertDto>>> getCriticalFacilityAlerts(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        
        log.debug("Getting critical alerts for facility: {}", facilityId);
        
        List<com.example.coffeemachine.domain.Alert> alerts = alertService.getCriticalAlertsByFacilityId(facilityId);
        List<AlertDto> alertDtos = alertMapper.toDto(alerts);
        
        return ResponseEntity.ok(ApiResponse.success(alertDtos, "Critical alerts retrieved successfully"));
    }

    /**
     * Get current user's facility information.
     * For facility users to get their assigned facility details.
     *
     * @return current user's facility
     */
    @GetMapping("/my-facility")
    @Operation(summary = "Get current user's facility", description = "Get facility information for the current facility user")
    @PreAuthorize("hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<FacilityDto>> getCurrentUserFacility() {
        UserPrincipal currentUser = authenticationService.getCurrentUserPrincipal();
        Long facilityId = currentUser.getFacilityId();
        
        if (facilityId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User is not assigned to a facility", "NO_FACILITY_ASSIGNED"));
        }
        
        return facilityService.findById(facilityId)
                .map(facility -> {
                    FacilityDto facilityDto = facilityMapper.toDto(facility);
                    return ResponseEntity.ok(ApiResponse.success(facilityDto, "User facility retrieved successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}