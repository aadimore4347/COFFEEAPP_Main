package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.domain.BrewType;
import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.MachineStatus;
import com.example.coffeemachine.repository.CoffeeMachineRepository;
import com.example.coffeemachine.repository.FacilityRepository;
import com.example.coffeemachine.repository.UsageHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for MQTT functionality using Testcontainers.
 * 
 * Tests the complete MQTT flow:
 * - Message publishing to broker
 * - Message reception and processing
 * - Database updates
 * - Command publishing
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class MqttIntegrationTest {

    @Container
    static GenericContainer<?> mosquitto = new GenericContainer<>(DockerImageName.parse("eclipse-mosquitto:2.0"))
            .withExposedPorts(1883)
            .withCommand("mosquitto", "-c", "/mosquitto-no-auth.conf");

    @DynamicPropertySource
    static void configureMqtt(DynamicPropertyRegistry registry) {
        registry.add("mqtt.broker.url", () -> "tcp://localhost:" + mosquitto.getMappedPort(1883));
        registry.add("mqtt.broker.client-id", () -> "test-client");
        registry.add("mqtt.broker.clean-session", () -> "true");
    }

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private CoffeeMachineRepository coffeeMachineRepository;

    @Autowired
    private UsageHistoryRepository usageHistoryRepository;

    @Autowired
    private MqttPublisherService mqttPublisherService;

    @Autowired
    private ObjectMapper objectMapper;

    private MqttClient testClient;
    private Facility testFacility;
    private CoffeeMachine testMachine;

    @BeforeEach
    void setUp() throws Exception {
        // Set up test data
        testFacility = new Facility("Test Facility", "Test Location");
        facilityRepository.save(testFacility);

        testMachine = new CoffeeMachine(testFacility);
        testMachine.updateStatus(MachineStatus.OFF);
        testMachine.updateTemperature(25.0);
        testMachine.updateWaterLevel(50);
        testMachine.updateMilkLevel(50);
        testMachine.updateBeansLevel(50);
        coffeeMachineRepository.save(testMachine);

        // Set up MQTT test client
        String brokerUrl = "tcp://localhost:" + mosquitto.getMappedPort(1883);
        testClient = new MqttClient(brokerUrl, "test-publisher");
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        testClient.connect(options);
    }

    @Test
    void shouldUpdateMachineTemperatureFromMqttMessage() throws Exception {
        // Given
        String topic = "coffeeMachine/" + testMachine.getId() + "/temperature";
        String payload = "92.5";

        // When
        publishMessage(topic, payload);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            CoffeeMachine updated = coffeeMachineRepository.findById(testMachine.getId()).orElseThrow();
            assertThat(updated.getTemperature()).isEqualTo(92.5);
        });
    }

    @Test
    void shouldUpdateMachineTemperatureFromJsonPayload() throws Exception {
        // Given
        String topic = "coffeeMachine/" + testMachine.getId() + "/temperature";
        Map<String, Object> payload = Map.of(
            "temperature", 93.2,
            "timestamp", "2024-01-15T10:30:00"
        );
        String jsonPayload = objectMapper.writeValueAsString(payload);

        // When
        publishMessage(topic, jsonPayload);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            CoffeeMachine updated = coffeeMachineRepository.findById(testMachine.getId()).orElseThrow();
            assertThat(updated.getTemperature()).isEqualTo(93.2);
        });
    }

    @Test
    void shouldUpdateSupplyLevels() throws Exception {
        // Given
        Long machineId = testMachine.getId();

        // When - Update water level
        publishMessage("coffeeMachine/" + machineId + "/waterLevel", "85");
        
        // Update milk level
        publishMessage("coffeeMachine/" + machineId + "/milkLevel", "75");
        
        // Update beans level
        publishMessage("coffeeMachine/" + machineId + "/beansLevel", "90");

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            CoffeeMachine updated = coffeeMachineRepository.findById(machineId).orElseThrow();
            assertThat(updated.getWaterLevel()).isEqualTo(85);
            assertThat(updated.getMilkLevel()).isEqualTo(75);
            assertThat(updated.getBeansLevel()).isEqualTo(90);
        });
    }

    @Test
    void shouldUpdateMachineStatus() throws Exception {
        // Given
        String topic = "coffeeMachine/" + testMachine.getId() + "/status";

        // When
        publishMessage(topic, "ON");

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            CoffeeMachine updated = coffeeMachineRepository.findById(testMachine.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(MachineStatus.ON);
        });
    }

    @Test
    void shouldRecordUsageHistoryFromMqttMessage() throws Exception {
        // Given
        String topic = "coffeeMachine/" + testMachine.getId() + "/usage";
        Map<String, Object> usagePayload = Map.of(
            "brewType", "ESPRESSO",
            "volume", 30,
            "temperature", 92.5,
            "timestamp", "2024-01-15T10:30:00"
        );
        String jsonPayload = objectMapper.writeValueAsString(usagePayload);

        // When
        publishMessage(topic, jsonPayload);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var usageHistory = usageHistoryRepository.findActiveByMachineIdOrderByTimestampDesc(testMachine.getId());
            assertThat(usageHistory).hasSize(1);
            assertThat(usageHistory.get(0).getBrewType()).isEqualTo(BrewType.ESPRESSO);
            assertThat(usageHistory.get(0).getVolumeMl()).isEqualTo(30);
            assertThat(usageHistory.get(0).getTempAtBrew()).isEqualTo(92.5);
        });
    }

    @Test
    void shouldRecordUsageHistoryFromPlainTextPayload() throws Exception {
        // Given
        String topic = "coffeeMachine/" + testMachine.getId() + "/usage";
        String payload = "CAPPUCCINO";

        // When
        publishMessage(topic, payload);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var usageHistory = usageHistoryRepository.findActiveByMachineIdOrderByTimestampDesc(testMachine.getId());
            assertThat(usageHistory).hasSize(1);
            assertThat(usageHistory.get(0).getBrewType()).isEqualTo(BrewType.CAPPUCCINO);
            assertThat(usageHistory.get(0).getVolumeMl()).isNull(); // Not provided in plain text
        });
    }

    @Test
    void shouldIgnoreInvalidPayloads() throws Exception {
        // Given
        Long machineId = testMachine.getId();
        String temperatureTopic = "coffeeMachine/" + machineId + "/temperature";
        String statusTopic = "coffeeMachine/" + machineId + "/status";

        // When - Send invalid payloads
        publishMessage(temperatureTopic, "invalid_temperature");
        publishMessage(statusTopic, "INVALID_STATUS");

        // Then - Machine should remain unchanged
        await().during(2, TimeUnit.SECONDS).untilAsserted(() -> {
            CoffeeMachine machine = coffeeMachineRepository.findById(machineId).orElseThrow();
            assertThat(machine.getTemperature()).isEqualTo(25.0); // Original value
            assertThat(machine.getStatus()).isEqualTo(MachineStatus.OFF); // Original value
        });
    }

    @Test
    void shouldIgnoreMessagesForNonexistentMachines() throws Exception {
        // Given
        Long nonExistentMachineId = 99999L;
        String topic = "coffeeMachine/" + nonExistentMachineId + "/temperature";

        // When
        publishMessage(topic, "92.5");

        // Then - No error should occur, message should be ignored
        await().during(2, TimeUnit.SECONDS).untilAsserted(() -> {
            // Verify our test machine is unchanged
            CoffeeMachine machine = coffeeMachineRepository.findById(testMachine.getId()).orElseThrow();
            assertThat(machine.getTemperature()).isEqualTo(25.0);
        });
    }

    @Test
    void shouldSendBrewCommandSuccessfully() {
        // When
        boolean result = mqttPublisherService.sendBrewCommand(testMachine.getId(), BrewType.ESPRESSO, 30);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldSendMaintenanceCommandSuccessfully() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cleaningCycle", "deep");
        parameters.put("duration", "30min");

        // When
        boolean result = mqttPublisherService.sendMaintenanceCommand(testMachine.getId(), "clean", parameters);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldSendStatusRequestSuccessfully() {
        // When
        boolean result = mqttPublisherService.requestStatus(testMachine.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldBroadcastCommandSuccessfully() {
        // Given
        Map<String, Object> parameters = Map.of("message", "System maintenance in 1 hour");

        // When
        boolean result = mqttPublisherService.broadcastCommand("announcement", parameters);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldHandleMultipleSimultaneousUpdates() throws Exception {
        // Given
        Long machineId = testMachine.getId();

        // When - Send multiple updates simultaneously
        publishMessage("coffeeMachine/" + machineId + "/temperature", "91.5");
        publishMessage("coffeeMachine/" + machineId + "/waterLevel", "75");
        publishMessage("coffeeMachine/" + machineId + "/status", "ON");
        publishMessage("coffeeMachine/" + machineId + "/usage", "LATTE");

        // Then - All updates should be processed
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            CoffeeMachine updated = coffeeMachineRepository.findById(machineId).orElseThrow();
            assertThat(updated.getTemperature()).isEqualTo(91.5);
            assertThat(updated.getWaterLevel()).isEqualTo(75);
            assertThat(updated.getStatus()).isEqualTo(MachineStatus.ON);

            var usageHistory = usageHistoryRepository.findActiveByMachineIdOrderByTimestampDesc(machineId);
            assertThat(usageHistory).hasSize(1);
            assertThat(usageHistory.get(0).getBrewType()).isEqualTo(BrewType.LATTE);
        });
    }

    private void publishMessage(String topic, String payload) throws Exception {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        testClient.publish(topic, message);
    }
}