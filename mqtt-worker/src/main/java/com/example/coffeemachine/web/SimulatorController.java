package com.example.coffeemachine.web;

import com.example.coffeemachine.simulator.SensorDataSimulator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for monitoring and controlling the sensor data simulator.
 * Provides endpoints to view simulation statistics and control simulation behavior.
 */
@RestController
@RequestMapping("/api/simulator")
@Slf4j
public class SimulatorController {

    private final SensorDataSimulator sensorDataSimulator;

    @Autowired
    public SimulatorController(SensorDataSimulator sensorDataSimulator) {
        this.sensorDataSimulator = sensorDataSimulator;
    }

    /**
     * Get current simulation statistics and machine states.
     *
     * @return simulation statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSimulationStats() {
        log.info("Simulation statistics requested");
        
        try {
            Map<String, Object> stats = sensorDataSimulator.getSimulationStats();
            return ResponseEntity.ok()
                .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(stats);
        } catch (Exception e) {
            log.error("Error retrieving simulation stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve simulation statistics",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Manually trigger sensor data generation.
     *
     * @return success message
     */
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerDataGeneration() {
        log.info("Manual data generation triggered");
        
        try {
            sensorDataSimulator.triggerDataGeneration();
            return ResponseEntity.ok(Map.of(
                "message", "Sensor data generation triggered successfully",
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error triggering data generation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to trigger data generation",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Reset all machine states to initial values.
     *
     * @return success message
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetMachineStates() {
        log.info("Machine states reset requested");
        
        try {
            sensorDataSimulator.resetMachineStates();
            return ResponseEntity.ok(Map.of(
                "message", "Machine states reset successfully",
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error resetting machine states: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to reset machine states",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get health status of the simulator.
     *
     * @return simulator health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSimulatorHealth() {
        log.debug("Simulator health check requested");
        
        try {
            Map<String, Object> stats = sensorDataSimulator.getSimulationStats();
            boolean isHealthy = (Boolean) stats.get("enabled");
            
            Map<String, Object> health = Map.of(
                "status", isHealthy ? "HEALTHY" : "DISABLED",
                "enabled", isHealthy,
                "timestamp", java.time.LocalDateTime.now().toString(),
                "machines", stats.get("machines"),
                "totalMessages", stats.get("totalMessagesSent")
            );
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error checking simulator health: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "UNHEALTHY",
                "error", "Failed to check simulator health",
                "message", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * Get detailed information about a specific machine.
     *
     * @param machineId the machine ID
     * @return machine details
     */
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<Map<String, Object>> getMachineDetails(@PathVariable Long machineId) {
        log.info("Machine details requested for machine ID: {}", machineId);
        
        try {
            Map<String, Object> stats = sensorDataSimulator.getSimulationStats();
            Map<String, Object> machines = (Map<String, Object>) stats.get("machines");
            
            String machineKey = "Machine_" + machineId;
            if (machines.containsKey(machineKey)) {
                Map<String, Object> machineInfo = (Map<String, Object>) machines.get(machineKey);
                return ResponseEntity.ok(machineInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving machine details for machine {}: {}", machineId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve machine details",
                "message", e.getMessage(),
                "machineId", machineId
            ));
        }
    }
}