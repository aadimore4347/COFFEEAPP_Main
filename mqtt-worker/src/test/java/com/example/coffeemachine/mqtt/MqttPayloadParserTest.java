package com.example.coffeemachine.mqtt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MQTT Payload Parser.
 */
class MqttPayloadParserTest {

    private MqttPayloadParser parser;

    @BeforeEach
    void setUp() {
        parser = new MqttPayloadParser();
    }

    @Test
    @DisplayName("Should parse valid temperature payload")
    void parseTemperature_ValidPayload_ReturnsTemperature() {
        // Given
        String payload = "{\"temperature\": 85.5}";

        // When
        BigDecimal result = parser.parseTemperature(payload);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("85.5"), result);
    }

    @Test
    @DisplayName("Should parse valid water level payload")
    void parseLevel_ValidWaterLevel_ReturnsLevel() {
        // Given
        String payload = "{\"waterLevel\": 75}";

        // When
        Integer result = parser.parseLevel(payload);

        // Then
        assertNotNull(result);
        assertEquals(75, result);
    }

    @Test
    @DisplayName("Should parse valid status payload")
    void parseStatus_ValidStatus_ReturnsStatus() {
        // Given
        String payload = "{\"status\": \"ON\"}";

        // When
        String result = parser.parseStatus(payload);

        // Then
        assertNotNull(result);
        assertEquals("ON", result);
    }

    @Test
    @DisplayName("Should parse valid usage event payload")
    void parseUsageEvent_ValidPayload_ReturnsUsageEvent() {
        // Given
        String payload = "{\"brewType\": \"ESPRESSO\", \"volumeMl\": 30, \"tempAtBrew\": 92.0}";

        // When
        MqttPayloadParser.UsageEvent result = parser.parseUsageEvent(payload);

        // Then
        assertNotNull(result);
        assertEquals("ESPRESSO", result.brewType());
        assertEquals(30, result.volumeMl());
        assertEquals(new BigDecimal("92.0"), result.tempAtBrew());
    }

    @Test
    @DisplayName("Should handle invalid JSON gracefully")
    void parseTemperature_InvalidJson_ReturnsNull() {
        // Given
        String payload = "invalid json";

        // When
        BigDecimal result = parser.parseTemperature(payload);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle missing fields gracefully")
    void parseTemperature_MissingField_ReturnsNull() {
        // Given
        String payload = "{\"otherField\": \"value\"}";

        // When
        BigDecimal result = parser.parseTemperature(payload);

        // Then
        assertNull(result);
    }
}