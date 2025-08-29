package com.example.coffeemachine.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates coffee machine sensor data and sends it via MQTT.
 * Generates realistic decreasing values for water, milk, beans, and temperature variations.
 */
@Component
@Slf4j
public class SensorDataSimulator {

    @Value("${spring.mqtt.broker.url:tcp://localhost:1883}")
    private String brokerUrl;

    @Value("${spring.mqtt.broker.client-id:coffee-machine-simulator}")
    private String clientId;

    @Value("${spring.mqtt.broker.username:}")
    private String username;

    @Value("${spring.mqtt.broker.password:}")
    private String password;

    @Value("${simulator.enabled:true}")
    private boolean enabled;

    @Value("${simulator.interval-ms:30000}")
    private long intervalMs;

    @Value("${simulator.machines:5}")
    private int numberOfMachines;

    @Value("${simulator.decrease-rates.water:1.0}")
    private double waterDecreaseRate;

    @Value("${simulator.decrease-rates.milk:0.5}")
    private double milkDecreaseRate;

    @Value("${simulator.decrease-rates.beans:0.3}")
    private double beansDecreaseRate;

    @Value("${simulator.temperature.variation:2.0}")
    private double temperatureVariation;

    @Value("${simulator.usage.probability:0.3}")
    private double usageProbability;

    private MqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private final Map<Long, MachineState> machineStates = new ConcurrentHashMap<>();
    private final AtomicInteger messageCounter = new AtomicInteger(0);

    /**
     * Represents the current state of a simulated coffee machine.
     */
    private class MachineState {
        double waterLevel;
        double milkLevel;
        double beansLevel;
        double temperature;
        String status;
        LocalDateTime lastUsage;
        int usageCount;

        MachineState() {
            // Initialize with random realistic values
            this.waterLevel = 80 + random.nextDouble() * 20; // 80-100%
            this.milkLevel = 70 + random.nextDouble() * 25;  // 70-95%
            this.beansLevel = 75 + random.nextDouble() * 20; // 75-95%
            this.temperature = 22 + random.nextDouble() * 3;  // 22-25°C (ambient)
            this.status = "OFF";
            this.lastUsage = LocalDateTime.now().minusHours(random.nextInt(24));
            this.usageCount = random.nextInt(50);
        }
    }

    @PostConstruct
    public void initialize() {
        if (!enabled) {
            log.info("Sensor data simulator is disabled");
            return;
        }

        try {
            // Initialize machine states early so stats are available even if MQTT is down
            initializeMachineStates();

            // Initialize MQTT client
            mqttClient = new MqttClient(brokerUrl, clientId + "-simulator", new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);

            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }

            mqttClient.connect(options);
            log.info("MQTT client connected successfully to {}", brokerUrl);

            log.info("Sensor data simulator initialized with {} machines, interval: {}ms", 
                    numberOfMachines, intervalMs);

        } catch (MqttException e) {
            log.error("Failed to initialize MQTT client: {}", e.getMessage(), e);
            // Ensure machine states exist even without MQTT connection
            if (machineStates.isEmpty()) {
                initializeMachineStates();
            }
        }
    }

    /**
     * Initialize machine states. This method can be called from tests.
     */
    void initializeMachineStates() {
        machineStates.clear();
        for (long i = 1; i <= numberOfMachines; i++) {
            machineStates.put(i, new MachineState());
        }
        log.debug("Initialized {} machine states", numberOfMachines);
    }

    @PreDestroy
    public void cleanup() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                log.info("MQTT client disconnected");
            } catch (MqttException e) {
                log.error("Error disconnecting MQTT client: {}", e.getMessage());
            }
        }
    }

    /**
     * Scheduled task that generates and sends sensor data every interval.
     */
    @Scheduled(fixedRateString = "${simulator.interval-ms:30000}")
    public void generateAndSendSensorData() {
        if (!enabled) {
            return;
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            log.debug("Generating sensor data for {} machines at {}", numberOfMachines, 
                    now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            for (Map.Entry<Long, MachineState> entry : machineStates.entrySet()) {
                Long machineId = entry.getKey();
                MachineState state = entry.getValue();

                // Update machine state (decrease levels, vary temperature)
                updateMachineState(state, now);

                // Send sensor data via MQTT if connected; otherwise, only update internal state
                if (mqttClient != null && mqttClient.isConnected()) {
                    sendSensorData(machineId, state, now);
                }

                // Randomly generate usage events
                if (random.nextDouble() < usageProbability) {
                    generateUsageEvent(machineId, state, now);
                }
            }

            log.info("Generated and sent sensor data for {} machines. Total messages: {}", 
                    numberOfMachines, messageCounter.get());

        } catch (Exception e) {
            log.error("Error generating sensor data: {}", e.getMessage(), e);
        }
    }

    /**
     * Updates the machine state by decreasing levels and varying temperature.
     */
    private void updateMachineState(MachineState state, LocalDateTime now) {
        // Decrease water level (faster when machine is ON)
        if ("ON".equals(state.status)) {
            state.waterLevel = Math.max(0, state.waterLevel - waterDecreaseRate);
        } else {
            state.waterLevel = Math.max(0, state.waterLevel - (waterDecreaseRate * 0.1));
        }

        // Decrease milk level (faster when machine is ON)
        if ("ON".equals(state.status)) {
            state.milkLevel = Math.max(0, state.milkLevel - milkDecreaseRate);
        } else {
            state.milkLevel = Math.max(0, state.milkLevel - (milkDecreaseRate * 0.1));
        }

        // Decrease beans level (faster when machine is ON)
        if ("ON".equals(state.status)) {
            state.beansLevel = Math.max(0, state.beansLevel - beansDecreaseRate);
        } else {
            state.beansLevel = Math.max(0, state.beansLevel - (beansDecreaseRate * 0.1));
        }

        // Vary temperature based on status
        if ("ON".equals(state.status)) {
            // When ON, temperature fluctuates around 90-95°C
            double baseTemp = 92.0;
            double variation = (random.nextDouble() - 0.5) * temperatureVariation;
            state.temperature = Math.max(85, Math.min(98, baseTemp + variation));
        } else {
            // When OFF, temperature gradually cools to ambient
            double ambientTemp = 22.0;
            if (state.temperature > ambientTemp + 2) {
                state.temperature = Math.max(ambientTemp, state.temperature - 0.5);
            }
        }

        // Randomly change status
        if (random.nextDouble() < 0.05) { // 5% chance to change status
            if ("ON".equals(state.status)) {
                state.status = random.nextDouble() < 0.1 ? "ERROR" : "OFF"; // 10% chance of ERROR
            } else if ("OFF".equals(state.status)) {
                state.status = "ON";
            } else { // ERROR status
                state.status = "OFF";
            }
        }
    }

    /**
     * Sends sensor data for a specific machine via MQTT.
     */
    private void sendSensorData(Long machineId, MachineState state, LocalDateTime timestamp) {
        try {
            // Send temperature data
            sendMqttMessage("coffeeMachine/" + machineId + "/temperature", 
                    createTemperaturePayload(state.temperature));

            // Send water level data
            sendMqttMessage("coffeeMachine/" + machineId + "/waterLevel", 
                    createLevelPayload("waterLevel", state.waterLevel));

            // Send milk level data
            sendMqttMessage("coffeeMachine/" + machineId + "/milkLevel", 
                    createLevelPayload("milkLevel", state.milkLevel));

            // Send beans level data
            sendMqttMessage("coffeeMachine/" + machineId + "/beansLevel", 
                    createLevelPayload("beansLevel", state.beansLevel));

            // Send status data
            sendMqttMessage("coffeeMachine/" + machineId + "/status", 
                    createStatusPayload(state.status));

            messageCounter.incrementAndGet();

        } catch (Exception e) {
            log.error("Error sending sensor data for machine {}: {}", machineId, e.getMessage());
        }
    }

    /**
     * Generates a random usage event for a machine.
     */
    private void generateUsageEvent(Long machineId, MachineState state, LocalDateTime timestamp) {
        if (!"ON".equals(state.status)) {
            return; // Only generate usage when machine is ON
        }

        try {
            // Define brew types and their characteristics
            String[] brewTypes = {"ESPRESSO", "CAPPUCCINO", "LATTE", "AMERICANO", "FLAT_WHITE", "MOCHA"};
            Map<String, Integer> brewVolumes = Map.of(
                "ESPRESSO", 30,
                "CAPPUCCINO", 150,
                "LATTE", 200,
                "AMERICANO", 250,
                "FLAT_WHITE", 160,
                "MOCHA", 200
            );

            String brewType = brewTypes[random.nextInt(brewTypes.length)];
            Integer volumeMl = brewVolumes.get(brewType);
            double tempAtBrew = state.temperature + (random.nextDouble() - 0.5) * 2;

            // Create usage event payload
            Map<String, Object> usageEvent = new HashMap<>();
            usageEvent.put("brewType", brewType);
            usageEvent.put("volumeMl", volumeMl);
            usageEvent.put("tempAtBrew", Math.round(tempAtBrew * 10.0) / 10.0);
            usageEvent.put("timestamp", timestamp.toString());

            String payload = objectMapper.writeValueAsString(usageEvent);
            sendMqttMessage("coffeeMachine/" + machineId + "/usage", payload);

            // Update machine state
            state.lastUsage = timestamp;
            state.usageCount++;
            state.waterLevel = Math.max(0, state.waterLevel - (volumeMl * 0.1)); // Water consumption
            state.milkLevel = Math.max(0, state.milkLevel - (brewType.contains("MILK") ? 15 : 0)); // Milk consumption
            state.beansLevel = Math.max(0, state.beansLevel - 5); // Beans consumption

            log.debug("Generated usage event for machine {}: {} ({}ml) at {}°C", 
                    machineId, brewType, volumeMl, tempAtBrew);

        } catch (Exception e) {
            log.error("Error generating usage event for machine {}: {}", machineId, e.getMessage());
        }
    }

    /**
     * Creates a temperature payload.
     */
    private String createTemperaturePayload(double temperature) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("temperature", Math.round(temperature * 10.0) / 10.0);
            payload.put("unit", "Celsius");
            payload.put("timestamp", LocalDateTime.now().toString());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return String.format("{\"temperature\": %.1f, \"unit\": \"Celsius\"}", temperature);
        }
    }

    /**
     * Creates a level payload.
     */
    private String createLevelPayload(String levelType, double level) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put(levelType, Math.round(level));
            payload.put("unit", "percent");
            payload.put("timestamp", LocalDateTime.now().toString());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return String.format("{\"%s\": %.0f, \"unit\": \"percent\"}", levelType, level);
        }
    }

    /**
     * Creates a status payload.
     */
    private String createStatusPayload(String status) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", status);
            payload.put("timestamp", LocalDateTime.now().toString());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return String.format("{\"status\": \"%s\"}", status);
        }
    }

    /**
     * Sends a message to a specific MQTT topic.
     */
    private void sendMqttMessage(String topic, String payload) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            message.setRetained(false);
            
            mqttClient.publish(topic, message);
            
            log.debug("Sent MQTT message to topic {}: {}", topic, payload);
            
        } catch (MqttException e) {
            log.error("Failed to send MQTT message to topic {}: {}", topic, e.getMessage());
        }
    }

    /**
     * Gets the current simulation statistics.
     */
    public Map<String, Object> getSimulationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("enabled", enabled);
        stats.put("intervalMs", intervalMs);
        stats.put("numberOfMachines", numberOfMachines);
        stats.put("totalMessagesSent", messageCounter.get());
        stats.put("machineStates", machineStates.size());
        stats.put("lastUpdate", LocalDateTime.now().toString());
        
        // Add machine-specific stats
        Map<String, Object> machineStats = new HashMap<>();
        for (Map.Entry<Long, MachineState> entry : machineStates.entrySet()) {
            MachineState state = entry.getValue();
            Map<String, Object> machineInfo = new HashMap<>();
            machineInfo.put("waterLevel", Math.round(state.waterLevel));
            machineInfo.put("milkLevel", Math.round(state.milkLevel));
            machineInfo.put("beansLevel", Math.round(state.beansLevel));
            machineInfo.put("temperature", Math.round(state.temperature * 10.0) / 10.0);
            machineInfo.put("status", state.status);
            machineInfo.put("usageCount", state.usageCount);
            machineInfo.put("lastUsage", state.lastUsage.toString());
            
            machineStats.put("Machine_" + entry.getKey(), machineInfo);
        }
        stats.put("machines", machineStats);
        
        return stats;
    }

    /**
     * Manually triggers sensor data generation for testing.
     */
    public void triggerDataGeneration() {
        if (enabled) {
            log.info("Manually triggering sensor data generation");
            generateAndSendSensorData();
        }
    }

    /**
     * Resets all machine states to initial values.
     */
    public void resetMachineStates() {
        log.info("Resetting all machine states to initial values");
        machineStates.clear();
        for (long i = 1; i <= numberOfMachines; i++) {
            machineStates.put(i, new MachineState());
        }
        messageCounter.set(0);
    }
}