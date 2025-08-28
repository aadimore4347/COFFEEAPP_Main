package com.example.coffeemachine.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class MqttTestConfig {

    /**
     * Test configuration for MQTT testing
     * This can be extended to include test MQTT broker configuration
     * or mock MQTT services for unit testing
     */
    
    // Add test MQTT configuration beans here if needed
    // For example, mock MQTT client, test broker configuration, etc.
}