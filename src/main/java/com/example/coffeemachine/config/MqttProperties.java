package com.example.coffeemachine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for MQTT broker connection and topic management.
 * 
 * Binds configuration values from application.yml under the 'mqtt' prefix.
 * Supports both local development (Mosquitto) and production (HiveMQ Cloud) configurations.
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    private Broker broker = new Broker();
    private Topics topics = new Topics();

    @Data
    public static class Broker {
        /**
         * MQTT broker URL (e.g., tcp://localhost:1883 or ssl://broker.hivemq.cloud:8883)
         */
        private String url = "tcp://localhost:1883";
        
        /**
         * Unique client identifier for this application instance
         */
        private String clientId = "coffee-machine-monitoring";
        
        /**
         * Username for broker authentication (optional for local dev)
         */
        private String username;
        
        /**
         * Password for broker authentication (optional for local dev)
         */
        private String password;
        
        /**
         * Whether to start with a clean session (true for dev, false for prod)
         */
        private boolean cleanSession = true;
        
        /**
         * Connection timeout in seconds
         */
        private int connectionTimeout = 30;
        
        /**
         * Keep-alive interval in seconds
         */
        private int keepAliveInterval = 60;
        
        /**
         * Maximum number of messages in flight
         */
        private int maxInflight = 10;
        
        /**
         * SSL configuration for secure connections
         */
        private Ssl ssl = new Ssl();
    }

    @Data
    public static class Ssl {
        /**
         * Whether SSL/TLS is enabled
         */
        private boolean enabled = false;
        
        /**
         * Path to trust store file
         */
        private String trustStore;
        
        /**
         * Trust store password
         */
        private String trustStorePassword;
    }

    @Data
    public static class Topics {
        /**
         * Topic pattern for temperature readings: coffeeMachine/{id}/temperature
         */
        private String temperature = "coffeeMachine/+/temperature";
        
        /**
         * Topic pattern for water level readings: coffeeMachine/{id}/waterLevel
         */
        private String waterLevel = "coffeeMachine/+/waterLevel";
        
        /**
         * Topic pattern for milk level readings: coffeeMachine/{id}/milkLevel
         */
        private String milkLevel = "coffeeMachine/+/milkLevel";
        
        /**
         * Topic pattern for beans level readings: coffeeMachine/{id}/beansLevel
         */
        private String beansLevel = "coffeeMachine/+/beansLevel";
        
        /**
         * Topic pattern for status updates: coffeeMachine/{id}/status
         */
        private String status = "coffeeMachine/+/status";
        
        /**
         * Topic pattern for usage events: coffeeMachine/{id}/usage
         */
        private String usage = "coffeeMachine/+/usage";
        
        /**
         * Topic pattern for sending commands: coffeeMachine/{id}/commands
         */
        private String commands = "coffeeMachine/+/commands";
    }

    /**
     * Get all subscription topics as an array for MQTT channel adapter configuration.
     * 
     * @return array of topic patterns for subscription
     */
    public String[] getSubscriptionTopics() {
        return new String[] {
            topics.temperature,
            topics.waterLevel,
            topics.milkLevel,
            topics.beansLevel,
            topics.status,
            topics.usage
        };
    }

    /**
     * Build the complete broker URL with credentials if provided.
     * 
     * @return formatted broker URL
     */
    public String getBrokerUrl() {
        return broker.url;
    }

    /**
     * Check if authentication is required (username and password provided).
     * 
     * @return true if authentication credentials are configured
     */
    public boolean hasAuthentication() {
        return broker.username != null && !broker.username.trim().isEmpty() &&
               broker.password != null && !broker.password.trim().isEmpty();
    }

    /**
     * Check if SSL is enabled.
     * 
     * @return true if SSL is configured
     */
    public boolean isSslEnabled() {
        return broker.ssl.enabled || broker.url.startsWith("ssl://");
    }

    /**
     * Generate a unique client ID for this instance.
     * Appends timestamp to avoid conflicts in development.
     * 
     * @return unique client ID
     */
    public String generateUniqueClientId() {
        return broker.clientId + "-" + System.currentTimeMillis();
    }

    /**
     * Create a command topic for a specific machine.
     * 
     * @param machineId the machine ID
     * @return command topic for the machine
     */
    public String getCommandTopic(Long machineId) {
        return topics.commands.replace("+", machineId.toString());
    }
}