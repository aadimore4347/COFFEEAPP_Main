package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.domain.BrewType;
import com.example.coffeemachine.domain.MachineStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for parsing MQTT message payloads.
 * 
 * Supports both JSON and plain text payload formats with robust error handling.
 * Provides type-safe parsing for all coffee machine telemetry data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttPayloadParser {

    private final ObjectMapper objectMapper;

    /**
     * Parse a temperature payload from MQTT message.
     * 
     * Supports:
     * - JSON: {"temperature": 92.5, "timestamp": "2024-01-15T10:30:00"}
     * - Plain text: "92.5"
     * 
     * @param payload the raw payload bytes
     * @return parsed temperature value or null if invalid
     */
    public Double parseTemperature(byte[] payload) {
        try {
            String payloadStr = new String(payload).trim();
            
            if (isJson(payloadStr)) {
                JsonNode json = objectMapper.readTree(payloadStr);
                if (json.has("temperature")) {
                    double temp = json.get("temperature").asDouble();
                    return isValidTemperature(temp) ? temp : null;
                }
            } else {
                // Plain text format
                double temp = Double.parseDouble(payloadStr);
                return isValidTemperature(temp) ? temp : null;
            }
        } catch (JsonProcessingException | NumberFormatException e) {
            log.warn("Failed to parse temperature payload: {}", new String(payload), e);
        }
        return null;
    }

    /**
     * Parse a supply level payload (water, milk, beans) from MQTT message.
     * 
     * Supports:
     * - JSON: {"level": 85, "timestamp": "2024-01-15T10:30:00"}
     * - Plain text: "85"
     * 
     * @param payload the raw payload bytes
     * @return parsed level value (0-100) or null if invalid
     */
    public Integer parseSupplyLevel(byte[] payload) {
        try {
            String payloadStr = new String(payload).trim();
            
            if (isJson(payloadStr)) {
                JsonNode json = objectMapper.readTree(payloadStr);
                if (json.has("level")) {
                    int level = json.get("level").asInt();
                    return isValidSupplyLevel(level) ? level : null;
                }
            } else {
                // Plain text format
                int level = Integer.parseInt(payloadStr);
                return isValidSupplyLevel(level) ? level : null;
            }
        } catch (JsonProcessingException | NumberFormatException e) {
            log.warn("Failed to parse supply level payload: {}", new String(payload), e);
        }
        return null;
    }

    /**
     * Parse a machine status payload from MQTT message.
     * 
     * Supports:
     * - JSON: {"status": "ON", "timestamp": "2024-01-15T10:30:00"}
     * - Plain text: "ON"
     * 
     * @param payload the raw payload bytes
     * @return parsed MachineStatus or null if invalid
     */
    public MachineStatus parseStatus(byte[] payload) {
        try {
            String payloadStr = new String(payload).trim();
            String statusStr;
            
            if (isJson(payloadStr)) {
                JsonNode json = objectMapper.readTree(payloadStr);
                if (json.has("status")) {
                    statusStr = json.get("status").asText();
                } else {
                    return null;
                }
            } else {
                // Plain text format
                statusStr = payloadStr;
            }
            
            return parseStatusString(statusStr);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse status payload: {}", new String(payload), e);
        }
        return null;
    }

    /**
     * Parse a usage event payload from MQTT message.
     * 
     * Supports:
     * - JSON: {
     *     "brewType": "ESPRESSO",
     *     "volume": 30,
     *     "temperature": 92.5,
     *     "timestamp": "2024-01-15T10:30:00"
     *   }
     * - Plain text: "ESPRESSO" (minimal format)
     * 
     * @param payload the raw payload bytes
     * @return parsed UsageEvent or null if invalid
     */
    public UsageEvent parseUsage(byte[] payload) {
        try {
            String payloadStr = new String(payload).trim();
            
            if (isJson(payloadStr)) {
                JsonNode json = objectMapper.readTree(payloadStr);
                
                if (!json.has("brewType")) {
                    return null;
                }
                
                String brewTypeStr = json.get("brewType").asText();
                BrewType brewType = parseBrewType(brewTypeStr);
                if (brewType == null) {
                    return null;
                }
                
                Integer volume = json.has("volume") ? json.get("volume").asInt() : null;
                Double temperature = json.has("temperature") ? json.get("temperature").asDouble() : null;
                LocalDateTime timestamp = json.has("timestamp") ? 
                    parseTimestamp(json.get("timestamp").asText()) : LocalDateTime.now();
                
                return new UsageEvent(brewType, volume, temperature, timestamp);
            } else {
                // Plain text format - just brew type
                BrewType brewType = parseBrewType(payloadStr);
                return brewType != null ? new UsageEvent(brewType, null, null, LocalDateTime.now()) : null;
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse usage payload: {}", new String(payload), e);
        }
        return null;
    }

    /**
     * Extract machine ID from MQTT topic.
     * 
     * Topic format: coffeeMachine/{machineId}/topicType
     * 
     * @param topic the MQTT topic
     * @return machine ID or null if invalid format
     */
    public Long extractMachineId(String topic) {
        try {
            String[] parts = topic.split("/");
            if (parts.length >= 2 && "coffeeMachine".equals(parts[0])) {
                return Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException e) {
            log.warn("Failed to extract machine ID from topic: {}", topic, e);
        }
        return null;
    }

    /**
     * Determine the topic type from MQTT topic.
     * 
     * @param topic the MQTT topic
     * @return topic type (temperature, waterLevel, etc.) or null
     */
    public String extractTopicType(String topic) {
        String[] parts = topic.split("/");
        return parts.length >= 3 ? parts[2] : null;
    }

    // Helper methods

    private boolean isJson(String payload) {
        return payload.trim().startsWith("{") && payload.trim().endsWith("}");
    }

    private boolean isValidTemperature(double temp) {
        return temp >= 0 && temp <= 150; // Reasonable temperature range for coffee machines
    }

    private boolean isValidSupplyLevel(int level) {
        return level >= 0 && level <= 100; // Percentage value
    }

    private MachineStatus parseStatusString(String statusStr) {
        try {
            return MachineStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid machine status: {}", statusStr);
            return null;
        }
    }

    private BrewType parseBrewType(String brewTypeStr) {
        try {
            return BrewType.valueOf(brewTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid brew type: {}", brewTypeStr);
            return null;
        }
    }

    private LocalDateTime parseTimestamp(String timestampStr) {
        try {
            return LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse timestamp: {}", timestampStr, e);
            return LocalDateTime.now();
        }
    }

    /**
     * Data class representing a parsed usage event.
     */
    public static class UsageEvent {
        public final BrewType brewType;
        public final Integer volume;
        public final Double temperature;
        public final LocalDateTime timestamp;

        public UsageEvent(BrewType brewType, Integer volume, Double temperature, LocalDateTime timestamp) {
            this.brewType = brewType;
            this.volume = volume;
            this.temperature = temperature;
            this.timestamp = timestamp;
        }
    }
}