package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.domain.UsageHistory;
import com.example.coffeemachine.repository.CoffeeMachineRepository;
import com.example.coffeemachine.repository.UsageHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for handling incoming MQTT messages from coffee machines.
 * 
 * Processes telemetry data and updates machine state in the database.
 * Routes messages based on topic type and validates machine existence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqttMessageHandler {

    private final MqttPayloadParser payloadParser;
    private final CoffeeMachineRepository coffeeMachineRepository;
    private final UsageHistoryRepository usageHistoryRepository;

    /**
     * Main message handler for all incoming MQTT messages.
     * 
     * Processes messages from the mqttInputChannel and routes them
     * to appropriate handlers based on topic type.
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    @Transactional
    public void handleMessage(Message<byte[]> message) {
        try {
            MessageHeaders headers = message.getHeaders();
            String topic = (String) headers.get("mqtt_receivedTopic");
            byte[] payload = message.getPayload();
            
            if (topic == null) {
                log.warn("Received MQTT message without topic header");
                return;
            }
            
            log.debug("Received MQTT message on topic: {} with payload: {}", topic, new String(payload));
            
            Long machineId = payloadParser.extractMachineId(topic);
            if (machineId == null) {
                log.warn("Could not extract machine ID from topic: {}", topic);
                return;
            }
            
            String topicType = payloadParser.extractTopicType(topic);
            if (topicType == null) {
                log.warn("Could not extract topic type from topic: {}", topic);
                return;
            }
            
            // Route to appropriate handler based on topic type
            switch (topicType) {
                case "temperature" -> handleTemperatureUpdate(machineId, payload);
                case "waterLevel" -> handleWaterLevelUpdate(machineId, payload);
                case "milkLevel" -> handleMilkLevelUpdate(machineId, payload);
                case "beansLevel" -> handleBeansLevelUpdate(machineId, payload);
                case "status" -> handleStatusUpdate(machineId, payload);
                case "usage" -> handleUsageEvent(machineId, payload);
                default -> log.warn("Unknown topic type: {} for topic: {}", topicType, topic);
            }
            
        } catch (Exception e) {
            log.error("Error processing MQTT message", e);
        }
    }

    /**
     * Handle temperature update messages.
     */
    private void handleTemperatureUpdate(Long machineId, byte[] payload) {
        Double temperature = payloadParser.parseTemperature(payload);
        if (temperature == null) {
            log.warn("Invalid temperature payload for machine {}: {}", machineId, new String(payload));
            return;
        }
        
        Optional<CoffeeMachine> machineOpt = coffeeMachineRepository.findActiveById(machineId);
        if (machineOpt.isEmpty()) {
            log.warn("Machine not found or inactive: {}", machineId);
            return;
        }
        
        CoffeeMachine machine = machineOpt.get();
        machine.updateTemperature(temperature);
        coffeeMachineRepository.save(machine);
        
        log.debug("Updated temperature for machine {}: {}°C", machineId, temperature);
    }

    /**
     * Handle water level update messages.
     */
    private void handleWaterLevelUpdate(Long machineId, byte[] payload) {
        Integer waterLevel = payloadParser.parseSupplyLevel(payload);
        if (waterLevel == null) {
            log.warn("Invalid water level payload for machine {}: {}", machineId, new String(payload));
            return;
        }
        
        Optional<CoffeeMachine> machineOpt = coffeeMachineRepository.findActiveById(machineId);
        if (machineOpt.isEmpty()) {
            log.warn("Machine not found or inactive: {}", machineId);
            return;
        }
        
        CoffeeMachine machine = machineOpt.get();
        machine.updateWaterLevel(waterLevel);
        coffeeMachineRepository.save(machine);
        
        log.debug("Updated water level for machine {}: {}%", machineId, waterLevel);
    }

    /**
     * Handle milk level update messages.
     */
    private void handleMilkLevelUpdate(Long machineId, byte[] payload) {
        Integer milkLevel = payloadParser.parseSupplyLevel(payload);
        if (milkLevel == null) {
            log.warn("Invalid milk level payload for machine {}: {}", machineId, new String(payload));
            return;
        }
        
        Optional<CoffeeMachine> machineOpt = coffeeMachineRepository.findActiveById(machineId);
        if (machineOpt.isEmpty()) {
            log.warn("Machine not found or inactive: {}", machineId);
            return;
        }
        
        CoffeeMachine machine = machineOpt.get();
        machine.updateMilkLevel(milkLevel);
        coffeeMachineRepository.save(machine);
        
        log.debug("Updated milk level for machine {}: {}%", machineId, milkLevel);
    }

    /**
     * Handle beans level update messages.
     */
    private void handleBeansLevelUpdate(Long machineId, byte[] payload) {
        Integer beansLevel = payloadParser.parseSupplyLevel(payload);
        if (beansLevel == null) {
            log.warn("Invalid beans level payload for machine {}: {}", machineId, new String(payload));
            return;
        }
        
        Optional<CoffeeMachine> machineOpt = coffeeMachineRepository.findActiveById(machineId);
        if (machineOpt.isEmpty()) {
            log.warn("Machine not found or inactive: {}", machineId);
            return;
        }
        
        CoffeeMachine machine = machineOpt.get();
        machine.updateBeansLevel(beansLevel);
        coffeeMachineRepository.save(machine);
        
        log.debug("Updated beans level for machine {}: {}%", machineId, beansLevel);
    }

    /**
     * Handle machine status update messages.
     */
    private void handleStatusUpdate(Long machineId, byte[] payload) {
        var status = payloadParser.parseStatus(payload);
        if (status == null) {
            log.warn("Invalid status payload for machine {}: {}", machineId, new String(payload));
            return;
        }
        
        Optional<CoffeeMachine> machineOpt = coffeeMachineRepository.findActiveById(machineId);
        if (machineOpt.isEmpty()) {
            log.warn("Machine not found or inactive: {}", machineId);
            return;
        }
        
        CoffeeMachine machine = machineOpt.get();
        machine.updateStatus(status);
        coffeeMachineRepository.save(machine);
        
        log.debug("Updated status for machine {}: {}", machineId, status);
    }

    /**
     * Handle usage event messages.
     */
    private void handleUsageEvent(Long machineId, byte[] payload) {
        MqttPayloadParser.UsageEvent usage = payloadParser.parseUsage(payload);
        if (usage == null) {
            log.warn("Invalid usage payload for machine {}: {}", machineId, new String(payload));
            return;
        }
        
        Optional<CoffeeMachine> machineOpt = coffeeMachineRepository.findActiveById(machineId);
        if (machineOpt.isEmpty()) {
            log.warn("Machine not found or inactive: {}", machineId);
            return;
        }
        
        CoffeeMachine machine = machineOpt.get();
        
        // Create usage history record
        UsageHistory usageHistory = new UsageHistory(
            machine,
            usage.timestamp,
            usage.brewType,
            usage.volume,
            usage.temperature
        );
        
        usageHistoryRepository.save(usageHistory);
        
        log.info("Recorded usage for machine {}: {} at {}", 
                machineId, usage.brewType, usage.timestamp);
    }
}