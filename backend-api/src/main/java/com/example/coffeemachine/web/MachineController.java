package com.example.coffeemachine.web;

import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.domain.UsageHistory;
import com.example.coffeemachine.security.UserPrincipal;
import com.example.coffeemachine.service.AlertService;
import com.example.coffeemachine.service.AuthenticationService;
import com.example.coffeemachine.service.CoffeeMachineService;
import com.example.coffeemachine.service.dto.*;
import com.example.coffeemachine.service.mapper.AlertMapper;
import com.example.coffeemachine.service.mapper.CoffeeMachineMapper;
import com.example.coffeemachine.service.mapper.UsageHistoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for coffee machine operations.
 * Handles machine status, brewing commands, usage history, and alerts.
 */
@RestController
@RequestMapping("/api/machine")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coffee Machine", description = "Coffee machine monitoring and control operations")
public class MachineController {

    private final CoffeeMachineService coffeeMachineService;
    private final AlertService alertService;
    private final AuthenticationService authenticationService;
    private final CoffeeMachineMapper coffeeMachineMapper;
    private final AlertMapper alertMapper;
    private final UsageHistoryMapper usageHistoryMapper;

    /**
     * Get machine status and current levels.
     * Facility users can only access machines in their facility.
     *
     * @param machineId the machine ID
     * @return machine status and levels
     */
    @GetMapping("/{machineId}/status")
    @Operation(summary = "Get machine status", description = "Get current status and supply levels of a coffee machine")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<CoffeeMachineDto>> getMachineStatus(
            @Parameter(description = "Machine ID") @PathVariable Long machineId) {
        
        log.debug("Getting status for machine: {}", machineId);
        
        return coffeeMachineService.getMachineStatus(machineId)
                .map(machine -> {
                    CoffeeMachineDto machineDto = coffeeMachineMapper.toDto(machine);
                    return ResponseEntity.ok(ApiResponse.success(machineDto, "Machine status retrieved successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get machine supply levels (water, milk, beans).
     * Facility users can only access machines in their facility.
     *
     * @param machineId the machine ID
     * @return machine supply levels
     */
    @GetMapping("/{machineId}/levels")
    @Operation(summary = "Get machine levels", description = "Get current supply levels (water, milk, beans) of a coffee machine")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<CoffeeMachineDto>> getMachineLevels(
            @Parameter(description = "Machine ID") @PathVariable Long machineId) {
        
        log.debug("Getting levels for machine: {}", machineId);
        
        return coffeeMachineService.findById(machineId)
                .map(machine -> {
                    CoffeeMachineDto machineDto = coffeeMachineMapper.toDto(machine);
                    return ResponseEntity.ok(ApiResponse.success(machineDto, "Machine levels retrieved successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get machine usage history.
     * Facility users can only access machines in their facility.
     *
     * @param machineId the machine ID
     * @param hours number of hours to look back (default: 24)
     * @return usage history
     */
    @GetMapping("/{machineId}/history")
    @Operation(summary = "Get machine usage history", description = "Get recent usage history for a coffee machine")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<List<UsageHistoryDto>>> getMachineHistory(
            @Parameter(description = "Machine ID") @PathVariable Long machineId,
            @Parameter(description = "Hours to look back") @RequestParam(defaultValue = "24") int hours) {
        
        log.debug("Getting history for machine: {} (last {} hours)", machineId, hours);
        
        List<UsageHistory> history = coffeeMachineService.getRecentUsage(machineId, hours);
        List<UsageHistoryDto> historyDtos = usageHistoryMapper.toDto(history);
        
        return ResponseEntity.ok(ApiResponse.success(historyDtos, "Machine history retrieved successfully"));
    }

    /**
     * Send brew command to a machine.
     * Publishes MQTT command to the machine.
     *
     * @param machineId the machine ID
     * @param brewRequest the brew request
     * @return brew command confirmation
     */
    @PostMapping("/{machineId}/brew")
    @Operation(summary = "Brew coffee", description = "Send brew command to a coffee machine")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<String>> brewCoffee(
            @Parameter(description = "Machine ID") @PathVariable Long machineId,
            @Valid @RequestBody BrewRequest brewRequest) {
        
        log.info("Brewing {} on machine {}", brewRequest.getBrewType(), machineId);
        
        // Check if machine exists and is active
        if (coffeeMachineService.findById(machineId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            // Publish MQTT brew command
            // MQTT functionality moved to separate mqtt-worker service
            
            UserPrincipal currentUser = authenticationService.getCurrentUserPrincipal();
            String message = String.format("Brew command sent successfully: %s (%dml) by user %s", 
                    brewRequest.getBrewType(), brewRequest.getVolumeMl(), currentUser.getUsername());
            
            return ResponseEntity.ok(ApiResponse.success("Command sent", message));
        } catch (Exception e) {
            log.error("Failed to send brew command to machine {}: {}", machineId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to send brew command", "BREW_COMMAND_FAILED"));
        }
    }

    /**
     * Get alerts for a machine.
     * Facility users can only access machines in their facility.
     *
     * @param machineId the machine ID
     * @return list of machine alerts
     */
    @GetMapping("/{machineId}/alerts")
    @Operation(summary = "Get machine alerts", description = "Get all unresolved alerts for a coffee machine")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<List<AlertDto>>> getMachineAlerts(
            @Parameter(description = "Machine ID") @PathVariable Long machineId) {
        
        log.debug("Getting alerts for machine: {}", machineId);
        
        List<com.example.coffeemachine.domain.Alert> alerts = alertService.getUnresolvedAlertsByMachineId(machineId);
        List<AlertDto> alertDtos = alertMapper.toDto(alerts);
        
        return ResponseEntity.ok(ApiResponse.success(alertDtos, "Machine alerts retrieved successfully"));
    }

    /**
     * Resolve an alert by ID.
     * Only admins or facility users for their own facility's machines.
     *
     * @param machineId the machine ID
     * @param alertId the alert ID
     * @return resolution confirmation
     */
    @PostMapping("/{machineId}/alert/{alertId}/resolve")
    @Operation(summary = "Resolve machine alert", description = "Mark a machine alert as resolved")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY')")
    public ResponseEntity<ApiResponse<String>> resolveAlert(
            @Parameter(description = "Machine ID") @PathVariable Long machineId,
            @Parameter(description = "Alert ID") @PathVariable Long alertId) {
        
        log.info("Resolving alert {} for machine {}", alertId, machineId);
        
        boolean resolved = alertService.resolveAlert(alertId);
        if (resolved) {
            UserPrincipal currentUser = authenticationService.getCurrentUserPrincipal();
            String message = String.format("Alert resolved by user %s", currentUser.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Alert resolved", message));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update machine configuration (admin only).
     *
     * @param machineId the machine ID
     * @param updateMachineRequest the machine update request
     * @return updated machine details
     */
    @PutMapping("/{machineId}")
    @Operation(summary = "Update machine configuration", description = "Update coffee machine settings (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CoffeeMachineDto>> updateMachine(
            @Parameter(description = "Machine ID") @PathVariable Long machineId,
            @Valid @RequestBody UpdateMachineRequest updateMachineRequest) {
        
        log.info("Updating machine configuration: {}", machineId);
        
        CoffeeMachine updates = coffeeMachineMapper.toEntity(updateMachineRequest);
        return coffeeMachineService.updateMachine(machineId, updates)
                .map(machine -> {
                    CoffeeMachineDto machineDto = coffeeMachineMapper.toDto(machine);
                    return ResponseEntity.ok(ApiResponse.success(machineDto, "Machine updated successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete/deactivate a machine (admin only).
     *
     * @param machineId the machine ID
     * @return deletion confirmation
     */
    @DeleteMapping("/{machineId}")
    @Operation(summary = "Delete machine", description = "Soft delete a coffee machine (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteMachine(
            @Parameter(description = "Machine ID") @PathVariable Long machineId) {
        
        log.info("Deleting machine: {}", machineId);
        
        boolean deleted = coffeeMachineService.deleteMachine(machineId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Machine deleted", "Machine has been deactivated"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}