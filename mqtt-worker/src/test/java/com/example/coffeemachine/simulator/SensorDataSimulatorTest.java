package com.example.coffeemachine.simulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Sensor Data Simulator.
 * Tests the core functionality without requiring a live MQTT broker.
 */
@ExtendWith(MockitoExtension.class)
class SensorDataSimulatorTest {

    private SensorDataSimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new SensorDataSimulator();
        
        // Set test configuration
        ReflectionTestUtils.setField(simulator, "enabled", true);
        ReflectionTestUtils.setField(simulator, "intervalMs", 1000L);
        ReflectionTestUtils.setField(simulator, "numberOfMachines", 3);
        ReflectionTestUtils.setField(simulator, "waterDecreaseRate", 1.0);
        ReflectionTestUtils.setField(simulator, "milkDecreaseRate", 0.5);
        ReflectionTestUtils.setField(simulator, "beansDecreaseRate", 0.3);
        ReflectionTestUtils.setField(simulator, "temperatureVariation", 2.0);
        ReflectionTestUtils.setField(simulator, "usageProbability", 0.3);
        
        // Manually initialize machine states since @PostConstruct won't run in tests
        ReflectionTestUtils.invokeMethod(simulator, "initializeMachineStates");
    }

    @Test
    @DisplayName("Should initialize simulator with correct configuration")
    void shouldInitializeSimulatorWithCorrectConfiguration() {
        // When
        Map<String, Object> stats = simulator.getSimulationStats();
        
        // Then
        assertNotNull(stats);
        assertEquals(true, stats.get("enabled"));
        assertEquals(1000L, stats.get("intervalMs"));
        assertEquals(3, stats.get("numberOfMachines"));
        assertEquals(0, stats.get("totalMessagesSent"));
        assertEquals(3, stats.get("machineStates"));
    }

    @Test
    @DisplayName("Should generate simulation statistics")
    void shouldGenerateSimulationStats() {
        // When
        Map<String, Object> stats = simulator.getSimulationStats();
        
        // Then
        assertNotNull(stats);
        assertTrue(stats.containsKey("machines"));
        assertTrue(stats.containsKey("lastUpdate"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> machines = (Map<String, Object>) stats.get("machines");
        assertEquals(3, machines.size());
        
        // Check that each machine has the required fields
        for (int i = 1; i <= 3; i++) {
            String machineKey = "Machine_" + i;
            assertTrue(machines.containsKey(machineKey));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> machineInfo = (Map<String, Object>) machines.get(machineKey);
            assertTrue(machineInfo.containsKey("waterLevel"));
            assertTrue(machineInfo.containsKey("milkLevel"));
            assertTrue(machineInfo.containsKey("beansLevel"));
            assertTrue(machineInfo.containsKey("temperature"));
            assertTrue(machineInfo.containsKey("status"));
            assertTrue(machineInfo.containsKey("usageCount"));
            assertTrue(machineInfo.containsKey("lastUsage"));
        }
    }

    @Test
    @DisplayName("Should reset machine states")
    void shouldResetMachineStates() {
        // Given
        Map<String, Object> initialStats = simulator.getSimulationStats();
        int initialMessageCount = (Integer) initialStats.get("totalMessagesSent");
        
        // When
        simulator.resetMachineStates();
        Map<String, Object> resetStats = simulator.getSimulationStats();
        
        // Then
        assertEquals(0, resetStats.get("totalMessagesSent"));
        assertEquals(3, resetStats.get("machineStates"));
        
        // Verify machines are reset
        @SuppressWarnings("unchecked")
        Map<String, Object> machines = (Map<String, Object>) resetStats.get("machines");
        for (int i = 1; i <= 3; i++) {
            String machineKey = "Machine_" + i;
            assertTrue(machines.containsKey(machineKey));
        }
    }

    @Test
    @DisplayName("Should handle manual trigger without errors")
    void shouldHandleManualTriggerWithoutErrors() {
        // When & Then - should not throw any exceptions
        assertDoesNotThrow(() -> simulator.triggerDataGeneration());
    }

    @Test
    @DisplayName("Should handle disabled simulator gracefully")
    void shouldHandleDisabledSimulatorGracefully() {
        // Given
        ReflectionTestUtils.setField(simulator, "enabled", false);
        
        // When
        Map<String, Object> stats = simulator.getSimulationStats();
        
        // Then
        assertEquals(false, stats.get("enabled"));
        // When disabled, machine states should still exist but not be actively managed
        assertEquals(3, stats.get("machineStates"));
    }

    @Test
    @DisplayName("Should maintain machine count consistency")
    void shouldMaintainMachineCountConsistency() {
        // When
        Map<String, Object> stats = simulator.getSimulationStats();
        
        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> machines = (Map<String, Object>) stats.get("machines");
        assertEquals(3, machines.size());
        assertEquals(3, stats.get("machineStates"));
        
        // Verify machine IDs are sequential
        for (int i = 1; i <= 3; i++) {
            assertTrue(machines.containsKey("Machine_" + i));
        }
    }

    @Test
    @DisplayName("Should provide valid machine data ranges")
    void shouldProvideValidMachineDataRanges() {
        // When
        Map<String, Object> stats = simulator.getSimulationStats();
        
        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> machines = (Map<String, Object>) stats.get("machines");
        
        for (int i = 1; i <= 3; i++) {
            String machineKey = "Machine_" + i;
            @SuppressWarnings("unchecked")
            Map<String, Object> machineInfo = (Map<String, Object>) machines.get(machineKey);
            assertNotNull(machineInfo, "Machine info should not be null for " + machineKey);
            
            // Check water level range (0-100) - handle both Integer and Long
            Object waterLevelObj = machineInfo.get("waterLevel");
            int waterLevel = waterLevelObj instanceof Long ? ((Long) waterLevelObj).intValue() : (Integer) waterLevelObj;
            assertTrue(waterLevel >= 0 && waterLevel <= 100);
            
            // Check milk level range (0-100) - handle both Integer and Long
            Object milkLevelObj = machineInfo.get("milkLevel");
            int milkLevel = milkLevelObj instanceof Long ? ((Long) milkLevelObj).intValue() : (Integer) milkLevelObj;
            assertTrue(milkLevel >= 0 && milkLevel <= 100);
            
            // Check beans level range (0-100) - handle both Integer and Long
            Object beansLevelObj = machineInfo.get("beansLevel");
            int beansLevel = beansLevelObj instanceof Long ? ((Long) beansLevelObj).intValue() : (Integer) beansLevelObj;
            assertTrue(beansLevel >= 0 && beansLevel <= 100);
            
            // Check temperature range (reasonable for coffee machines) - handle both Double and Long
            Object tempObj = machineInfo.get("temperature");
            double temperature = tempObj instanceof Long ? ((Long) tempObj).doubleValue() : (Double) tempObj;
            assertTrue(temperature >= 20 && temperature <= 100);
            
            // Check status is valid
            String status = (String) machineInfo.get("status");
            assertTrue(status.equals("ON") || status.equals("OFF") || status.equals("ERROR"));
        }
    }
}