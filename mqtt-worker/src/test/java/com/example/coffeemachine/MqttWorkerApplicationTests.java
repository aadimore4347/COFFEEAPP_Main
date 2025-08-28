package com.example.coffeemachine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic test for MQTT Worker Application.
 * Ensures the application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class MqttWorkerApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}