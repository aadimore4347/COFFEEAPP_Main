package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.domain.BrewType;
import com.example.coffeemachine.domain.MachineStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MQTT payload parsing functionality.
 * 
 * Tests both JSON and plain text payload formats for all message types.
 */
class MqttPayloadParserTest {

    private MqttPayloadParser parser;

    @BeforeEach
    void setUp() {
        parser = new MqttPayloadParser(new ObjectMapper());
    }

    @Test
    void shouldParseTemperatureFromPlainText() {
        // Given
        byte[] payload = "92.5".getBytes();

        // When
        Double temperature = parser.parseTemperature(payload);

        // Then
        assertThat(temperature).isEqualTo(92.5);
    }

    @Test
    void shouldParseTemperatureFromJson() {
        // Given
        String jsonPayload = "{\"temperature\": 93.2, \"timestamp\": \"2024-01-15T10:30:00\"}";
        byte[] payload = jsonPayload.getBytes();

        // When
        Double temperature = parser.parseTemperature(payload);

        // Then
        assertThat(temperature).isEqualTo(93.2);
    }

    @Test
    void shouldReturnNullForInvalidTemperature() {
        // Given
        byte[] invalidPayload = "invalid_temp".getBytes();

        // When
        Double temperature = parser.parseTemperature(invalidPayload);

        // Then
        assertThat(temperature).isNull();
    }

    @Test
    void shouldReturnNullForOutOfRangeTemperature() {
        // Given
        byte[] tooHotPayload = "200.0".getBytes();
        byte[] tooColPayload = "-10.0".getBytes();

        // When
        Double tooHot = parser.parseTemperature(tooHotPayload);
        Double tooCold = parser.parseTemperature(tooColPayload);

        // Then
        assertThat(tooHot).isNull();
        assertThat(tooCold).isNull();
    }

    @Test
    void shouldParseSupplyLevelFromPlainText() {
        // Given
        byte[] payload = "85".getBytes();

        // When
        Integer level = parser.parseSupplyLevel(payload);

        // Then
        assertThat(level).isEqualTo(85);
    }

    @Test
    void shouldParseSupplyLevelFromJson() {
        // Given
        String jsonPayload = "{\"level\": 75, \"timestamp\": \"2024-01-15T10:30:00\"}";
        byte[] payload = jsonPayload.getBytes();

        // When
        Integer level = parser.parseSupplyLevel(payload);

        // Then
        assertThat(level).isEqualTo(75);
    }

    @Test
    void shouldReturnNullForInvalidSupplyLevel() {
        // Given
        byte[] invalidPayload = "invalid_level".getBytes();

        // When
        Integer level = parser.parseSupplyLevel(invalidPayload);

        // Then
        assertThat(level).isNull();
    }

    @Test
    void shouldReturnNullForOutOfRangeSupplyLevel() {
        // Given
        byte[] tooHighPayload = "150".getBytes();
        byte[] negativePayload = "-5".getBytes();

        // When
        Integer tooHigh = parser.parseSupplyLevel(tooHighPayload);
        Integer negative = parser.parseSupplyLevel(negativePayload);

        // Then
        assertThat(tooHigh).isNull();
        assertThat(negative).isNull();
    }

    @Test
    void shouldParseStatusFromPlainText() {
        // Given
        byte[] onPayload = "ON".getBytes();
        byte[] offPayload = "OFF".getBytes();
        byte[] errorPayload = "ERROR".getBytes();

        // When
        MachineStatus onStatus = parser.parseStatus(onPayload);
        MachineStatus offStatus = parser.parseStatus(offPayload);
        MachineStatus errorStatus = parser.parseStatus(errorPayload);

        // Then
        assertThat(onStatus).isEqualTo(MachineStatus.ON);
        assertThat(offStatus).isEqualTo(MachineStatus.OFF);
        assertThat(errorStatus).isEqualTo(MachineStatus.ERROR);
    }

    @Test
    void shouldParseStatusFromJson() {
        // Given
        String jsonPayload = "{\"status\": \"ON\", \"timestamp\": \"2024-01-15T10:30:00\"}";
        byte[] payload = jsonPayload.getBytes();

        // When
        MachineStatus status = parser.parseStatus(payload);

        // Then
        assertThat(status).isEqualTo(MachineStatus.ON);
    }

    @Test
    void shouldReturnNullForInvalidStatus() {
        // Given
        byte[] invalidPayload = "INVALID_STATUS".getBytes();

        // When
        MachineStatus status = parser.parseStatus(invalidPayload);

        // Then
        assertThat(status).isNull();
    }

    @Test
    void shouldParseUsageFromCompleteJson() {
        // Given
        String jsonPayload = "{\n" +
                "  \"brewType\": \"ESPRESSO\",\n" +
                "  \"volume\": 30,\n" +
                "  \"temperature\": 92.5,\n" +
                "  \"timestamp\": \"2024-01-15T10:30:00\"\n" +
                "}";
        byte[] payload = jsonPayload.getBytes();

        // When
        MqttPayloadParser.UsageEvent usage = parser.parseUsage(payload);

        // Then
        assertThat(usage).isNotNull();
        assertThat(usage.brewType).isEqualTo(BrewType.ESPRESSO);
        assertThat(usage.volume).isEqualTo(30);
        assertThat(usage.temperature).isEqualTo(92.5);
        assertThat(usage.timestamp).isNotNull();
    }

    @Test
    void shouldParseUsageFromMinimalJson() {
        // Given
        String jsonPayload = "{\"brewType\": \"CAPPUCCINO\"}";
        byte[] payload = jsonPayload.getBytes();

        // When
        MqttPayloadParser.UsageEvent usage = parser.parseUsage(payload);

        // Then
        assertThat(usage).isNotNull();
        assertThat(usage.brewType).isEqualTo(BrewType.CAPPUCCINO);
        assertThat(usage.volume).isNull();
        assertThat(usage.temperature).isNull();
        assertThat(usage.timestamp).isNotNull(); // Should default to now
    }

    @Test
    void shouldParseUsageFromPlainText() {
        // Given
        byte[] payload = "LATTE".getBytes();

        // When
        MqttPayloadParser.UsageEvent usage = parser.parseUsage(payload);

        // Then
        assertThat(usage).isNotNull();
        assertThat(usage.brewType).isEqualTo(BrewType.LATTE);
        assertThat(usage.volume).isNull();
        assertThat(usage.temperature).isNull();
        assertThat(usage.timestamp).isNotNull();
    }

    @Test
    void shouldReturnNullForInvalidBrewType() {
        // Given
        byte[] invalidPayload = "INVALID_BREW_TYPE".getBytes();

        // When
        MqttPayloadParser.UsageEvent usage = parser.parseUsage(invalidPayload);

        // Then
        assertThat(usage).isNull();
    }

    @Test
    void shouldReturnNullForUsageWithoutBrewType() {
        // Given
        String jsonPayload = "{\"volume\": 30, \"temperature\": 92.5}";
        byte[] payload = jsonPayload.getBytes();

        // When
        MqttPayloadParser.UsageEvent usage = parser.parseUsage(payload);

        // Then
        assertThat(usage).isNull();
    }

    @Test
    void shouldExtractMachineIdFromTopic() {
        // Given
        String topic = "coffeeMachine/123/temperature";

        // When
        Long machineId = parser.extractMachineId(topic);

        // Then
        assertThat(machineId).isEqualTo(123L);
    }

    @Test
    void shouldReturnNullForInvalidTopicFormat() {
        // Given
        String invalidTopic = "invalid/topic/format";

        // When
        Long machineId = parser.extractMachineId(invalidTopic);

        // Then
        assertThat(machineId).isNull();
    }

    @Test
    void shouldReturnNullForNonNumericMachineId() {
        // Given
        String invalidTopic = "coffeeMachine/abc/temperature";

        // When
        Long machineId = parser.extractMachineId(invalidTopic);

        // Then
        assertThat(machineId).isNull();
    }

    @Test
    void shouldExtractTopicTypeFromTopic() {
        // Given
        String temperatureTopic = "coffeeMachine/123/temperature";
        String waterLevelTopic = "coffeeMachine/456/waterLevel";
        String statusTopic = "coffeeMachine/789/status";

        // When
        String tempType = parser.extractTopicType(temperatureTopic);
        String waterType = parser.extractTopicType(waterLevelTopic);
        String statusType = parser.extractTopicType(statusTopic);

        // Then
        assertThat(tempType).isEqualTo("temperature");
        assertThat(waterType).isEqualTo("waterLevel");
        assertThat(statusType).isEqualTo("status");
    }

    @Test
    void shouldReturnNullForTopicWithoutType() {
        // Given
        String shortTopic = "coffeeMachine/123";

        // When
        String topicType = parser.extractTopicType(shortTopic);

        // Then
        assertThat(topicType).isNull();
    }

    @Test
    void shouldHandleAllBrewTypes() {
        // Test all enum values can be parsed
        for (BrewType brewType : BrewType.values()) {
            // Given
            byte[] payload = brewType.name().getBytes();

            // When
            MqttPayloadParser.UsageEvent usage = parser.parseUsage(payload);

            // Then
            assertThat(usage).isNotNull();
            assertThat(usage.brewType).isEqualTo(brewType);
        }
    }

    @Test
    void shouldHandleCaseInsensitiveBrewTypes() {
        // Given
        byte[] lowerCasePayload = "espresso".getBytes();
        byte[] mixedCasePayload = "CaPpUcCiNo".getBytes();

        // When
        MqttPayloadParser.UsageEvent lowerUsage = parser.parseUsage(lowerCasePayload);
        MqttPayloadParser.UsageEvent mixedUsage = parser.parseUsage(mixedCasePayload);

        // Then
        assertThat(lowerUsage).isNotNull();
        assertThat(lowerUsage.brewType).isEqualTo(BrewType.ESPRESSO);
        assertThat(mixedUsage).isNotNull();
        assertThat(mixedUsage.brewType).isEqualTo(BrewType.CAPPUCCINO);
    }

    @Test
    void shouldHandleWhitespaceInPayloads() {
        // Given
        byte[] payloadWithSpaces = "  92.5  ".getBytes();
        byte[] statusWithSpaces = "  ON  ".getBytes();

        // When
        Double temperature = parser.parseTemperature(payloadWithSpaces);
        MachineStatus status = parser.parseStatus(statusWithSpaces);

        // Then
        assertThat(temperature).isEqualTo(92.5);
        assertThat(status).isEqualTo(MachineStatus.ON);
    }
}