package com.example.coffeemachine.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class MqttPayloadParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BigDecimal parseTemperature(String payload) {
        try {
            if (payload.trim().startsWith("{")) {
                Map<String, Object> json = objectMapper.readValue(payload, Map.class);
                Object tempValue = json.get("temperature");
                if (tempValue != null) {
                    return new BigDecimal(tempValue.toString());
                }
            }
            return new BigDecimal(payload.trim());
        } catch (Exception e) {
            log.warn("Failed to parse temperature from payload: {}", payload);
            return null;
        }
    }

    public Integer parseLevel(String payload) {
        try {
            if (payload.trim().startsWith("{")) {
                Map<String, Object> json = objectMapper.readValue(payload, Map.class);
                Object levelValue = json.get("level");
                if (levelValue != null) {
                    int level = Integer.parseInt(levelValue.toString());
                    return Math.max(0, Math.min(100, level));
                }
            }
            int level = Integer.parseInt(payload.trim());
            return Math.max(0, Math.min(100, level));
        } catch (Exception e) {
            log.warn("Failed to parse level from payload: {}", payload);
            return null;
        }
    }

    public String parseStatus(String payload) {
        try {
            if (payload.trim().startsWith("{")) {
                Map<String, Object> json = objectMapper.readValue(payload, Map.class);
                Object statusValue = json.get("status");
                if (statusValue != null) {
                    return statusValue.toString().toUpperCase();
                }
            }
            return payload.trim().toUpperCase();
        } catch (Exception e) {
            log.warn("Failed to parse status from payload: {}", payload);
            return null;
        }
    }

    public UsageEvent parseUsageEvent(String payload) {
        try {
            if (payload.trim().startsWith("{")) {
                Map<String, Object> json = objectMapper.readValue(payload, Map.class);
                String brewType = getStringValue(json, "brewType", "UNKNOWN");
                Integer volumeMl = getIntValue(json, "volumeMl", 0);
                BigDecimal tempAtBrew = getBigDecimalValue(json, "tempAtBrew");
                return new UsageEvent(brewType, volumeMl, tempAtBrew);
            }
            String[] parts = payload.split(":");
            if (parts.length >= 2) {
                String brewType = parts[0].trim();
                Integer volumeMl = parts.length > 1 ? parseIntSafe(parts[1].trim()) : 0;
                BigDecimal tempAtBrew = parts.length > 2 ? parseBigDecimalSafe(parts[2].trim()) : null;
                return new UsageEvent(brewType, volumeMl, tempAtBrew);
            }
            return null;
        } catch (Exception e) {
            log.warn("Failed to parse usage event from payload: {}", payload);
            return null;
        }
    }

    private String getStringValue(Map<String, Object> json, String key, String defaultValue) {
        Object value = json.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private Integer getIntValue(Map<String, Object> json, String key, Integer defaultValue) {
        Object value = json.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Failed to parse integer value for key {}: {}", key, value);
            }
        }
        return defaultValue;
    }

    private BigDecimal getBigDecimalValue(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value != null) {
            try {
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Failed to parse BigDecimal value for key {}: {}", key, value);
            }
        }
        return null;
    }

    private Integer parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private BigDecimal parseBigDecimalSafe(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static class UsageEvent {
        private final String brewType;
        private final Integer volumeMl;
        private final BigDecimal tempAtBrew;

        public UsageEvent(String brewType, Integer volumeMl, BigDecimal tempAtBrew) {
            this.brewType = brewType;
            this.volumeMl = volumeMl;
            this.tempAtBrew = tempAtBrew;
        }

        public String brewType() { return brewType; }
        public Integer volumeMl() { return volumeMl; }
        public BigDecimal tempAtBrew() { return tempAtBrew; }
    }
}
