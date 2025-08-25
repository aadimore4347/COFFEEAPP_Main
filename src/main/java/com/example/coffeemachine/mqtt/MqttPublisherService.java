package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.config.MqttProperties;
import com.example.coffeemachine.domain.BrewType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for publishing MQTT messages to coffee machines.
 * 
 * Provides high-level methods for sending commands to machines
 * with proper payload formatting and error handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqttPublisherService {

    private final MessageChannel mqttOutputChannel;
    private final MqttProperties mqttProperties;
    private final ObjectMapper objectMapper;

    /**
     * Send a brew command to a specific coffee machine.
     * 
     * @param machineId the target machine ID
     * @param brewType the type of beverage to brew
     * @param volumeMl the volume in milliliters (optional)
     * @return true if command was sent successfully
     */
    public boolean sendBrewCommand(Long machineId, BrewType brewType, Integer volumeMl) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", "brew");
            command.put("brewType", brewType.name());
            command.put("timestamp", LocalDateTime.now().toString());
            
            if (volumeMl != null && volumeMl > 0) {
                command.put("volume", volumeMl);
            }
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = mqttProperties.getCommandTopic(machineId);
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to send brew command to machine {}", machineId, e);
            return false;
        }
    }

    /**
     * Send a maintenance command to a coffee machine.
     * 
     * @param machineId the target machine ID
     * @param action the maintenance action (e.g., "clean", "descale", "refill")
     * @param parameters additional parameters for the action
     * @return true if command was sent successfully
     */
    public boolean sendMaintenanceCommand(Long machineId, String action, Map<String, Object> parameters) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", action);
            command.put("timestamp", LocalDateTime.now().toString());
            
            if (parameters != null && !parameters.isEmpty()) {
                command.put("parameters", parameters);
            }
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = mqttProperties.getCommandTopic(machineId);
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to send maintenance command to machine {}: {}", machineId, action, e);
            return false;
        }
    }

    /**
     * Send a configuration update command to a coffee machine.
     * 
     * @param machineId the target machine ID
     * @param config the configuration parameters
     * @return true if command was sent successfully
     */
    public boolean sendConfigurationCommand(Long machineId, Map<String, Object> config) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", "configure");
            command.put("timestamp", LocalDateTime.now().toString());
            command.put("config", config);
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = mqttProperties.getCommandTopic(machineId);
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to send configuration command to machine {}", machineId, e);
            return false;
        }
    }

    /**
     * Send a status request command to a coffee machine.
     * 
     * @param machineId the target machine ID
     * @return true if command was sent successfully
     */
    public boolean requestStatus(Long machineId) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", "status");
            command.put("timestamp", LocalDateTime.now().toString());
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = mqttProperties.getCommandTopic(machineId);
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to request status from machine {}", machineId, e);
            return false;
        }
    }

    /**
     * Send a reset command to a coffee machine.
     * 
     * @param machineId the target machine ID
     * @param resetType the type of reset ("soft", "hard", "factory")
     * @return true if command was sent successfully
     */
    public boolean sendResetCommand(Long machineId, String resetType) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", "reset");
            command.put("resetType", resetType);
            command.put("timestamp", LocalDateTime.now().toString());
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = mqttProperties.getCommandTopic(machineId);
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to send reset command to machine {}: {}", machineId, resetType, e);
            return false;
        }
    }

    /**
     * Send a supply refill notification to a coffee machine.
     * 
     * @param machineId the target machine ID
     * @param supplyType the type of supply ("water", "milk", "beans")
     * @param levelAfterRefill the new level after refill (0-100)
     * @return true if command was sent successfully
     */
    public boolean notifySupplyRefill(Long machineId, String supplyType, Integer levelAfterRefill) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", "refill");
            command.put("supplyType", supplyType);
            command.put("newLevel", levelAfterRefill);
            command.put("timestamp", LocalDateTime.now().toString());
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = mqttProperties.getCommandTopic(machineId);
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to send refill notification to machine {}: {} -> {}%", 
                     machineId, supplyType, levelAfterRefill, e);
            return false;
        }
    }

    /**
     * Send a custom command to a coffee machine.
     * 
     * @param machineId the target machine ID
     * @param payload the custom payload string
     * @return true if command was sent successfully
     */
    public boolean sendCustomCommand(Long machineId, String payload) {
        try {
            String topic = mqttProperties.getCommandTopic(machineId);
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to send custom command to machine {}", machineId, e);
            return false;
        }
    }

    /**
     * Broadcast a message to all coffee machines.
     * 
     * @param action the action to broadcast
     * @param parameters optional parameters
     * @return true if broadcast was sent successfully
     */
    public boolean broadcastCommand(String action, Map<String, Object> parameters) {
        try {
            Map<String, Object> command = new HashMap<>();
            command.put("action", action);
            command.put("timestamp", LocalDateTime.now().toString());
            command.put("broadcast", true);
            
            if (parameters != null && !parameters.isEmpty()) {
                command.put("parameters", parameters);
            }
            
            String payload = objectMapper.writeValueAsString(command);
            String topic = "coffeeMachine/broadcast/commands";
            
            return publishMessage(topic, payload);
            
        } catch (Exception e) {
            log.error("Failed to broadcast command: {}", action, e);
            return false;
        }
    }

    /**
     * Low-level method to publish a message to an MQTT topic.
     * 
     * @param topic the target topic
     * @param payload the message payload
     * @return true if message was sent successfully
     */
    private boolean publishMessage(String topic, String payload) {
        try {
            var message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .setHeader(MqttHeaders.RETAINED, false)
                .build();
            
            boolean sent = mqttOutputChannel.send(message, 5000); // 5 second timeout
            
            if (sent) {
                log.debug("Published MQTT message to topic {}: {}", topic, payload);
            } else {
                log.warn("Failed to publish MQTT message to topic {} (timeout)", topic);
            }
            
            return sent;
            
        } catch (Exception e) {
            log.error("Error publishing MQTT message to topic {}", topic, e);
            return false;
        }
    }
}