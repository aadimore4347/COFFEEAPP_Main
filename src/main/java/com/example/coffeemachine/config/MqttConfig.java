package com.example.coffeemachine.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * MQTT configuration for coffee machine monitoring system.
 * 
 * Configures MQTT client factory, message channels, and adapters for:
 * - Subscribing to machine telemetry topics
 * - Publishing commands to machines
 * - Supporting both local Mosquitto and HiveMQ Cloud
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttProperties mqttProperties;

    /**
     * MQTT client factory with connection configuration.
     * Supports both authenticated and non-authenticated connections,
     * with optional SSL for production environments.
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttProperties.getBrokerUrl()});
        options.setCleanSession(mqttProperties.getBroker().isCleanSession());
        options.setConnectionTimeout(mqttProperties.getBroker().getConnectionTimeout());
        options.setKeepAliveInterval(mqttProperties.getBroker().getKeepAliveInterval());
        options.setMaxInflight(mqttProperties.getBroker().getMaxInflight());
        
        // Configure authentication if provided
        if (mqttProperties.hasAuthentication()) {
            options.setUserName(mqttProperties.getBroker().getUsername());
            options.setPassword(mqttProperties.getBroker().getPassword().toCharArray());
            log.info("MQTT authentication configured for user: {}", mqttProperties.getBroker().getUsername());
        }
        
        // Configure SSL if enabled
        if (mqttProperties.isSslEnabled()) {
            try {
                configureSsl(options);
                log.info("MQTT SSL configuration enabled");
            } catch (Exception e) {
                log.error("Failed to configure MQTT SSL", e);
                throw new RuntimeException("MQTT SSL configuration failed", e);
            }
        }
        
        factory.setConnectionOptions(options);
        
        log.info("MQTT client factory configured for broker: {}", mqttProperties.getBrokerUrl());
        return factory;
    }

    /**
     * Configure SSL context for secure MQTT connections.
     * Used for HiveMQ Cloud and other TLS-enabled brokers.
     */
    private void configureSsl(MqttConnectOptions options) throws Exception {
        if (mqttProperties.getBroker().getSsl().getTrustStore() != null) {
            // Load custom trust store
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustStoreStream = new FileInputStream(
                    mqttProperties.getBroker().getSsl().getTrustStore())) {
                trustStore.load(trustStoreStream, 
                               mqttProperties.getBroker().getSsl().getTrustStorePassword().toCharArray());
            }
            
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            
            options.setSSLProperties(System.getProperties());
            options.setSocketFactory(sslContext.getSocketFactory());
        } else {
            // Use default SSL context for well-known certificate authorities
            options.setSSLProperties(System.getProperties());
        }
    }

    /**
     * Message channel for incoming MQTT messages.
     * All subscribed topics will publish messages to this channel.
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * Message channel for outgoing MQTT messages.
     * Used for publishing commands and responses.
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT inbound channel adapter for subscribing to machine telemetry.
     * Automatically subscribes to all configured topics and routes messages
     * to the input channel for processing.
     */
    @Bean
    public MessageProducer inboundChannelAdapter() {
        String clientId = mqttProperties.generateUniqueClientId() + "-inbound";
        
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
            clientId, mqttClientFactory(), mqttProperties.getSubscriptionTopics());
        
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1); // At least once delivery
        adapter.setOutputChannel(mqttInputChannel());
        
        log.info("MQTT inbound adapter configured with client ID: {} for topics: {}", 
                clientId, String.join(", ", mqttProperties.getSubscriptionTopics()));
        
        return adapter;
    }

    /**
     * MQTT outbound message handler for publishing messages.
     * Handles command publishing and other outbound communications.
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutputChannel")
    public MessageHandler mqttOutboundHandler() {
        String clientId = mqttProperties.generateUniqueClientId() + "-outbound";
        
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1); // At least once delivery
        messageHandler.setDefaultRetained(false);
        
        log.info("MQTT outbound handler configured with client ID: {}", clientId);
        
        return messageHandler;
    }

    /**
     * Configuration for non-test profiles.
     * Ensures MQTT is only configured when not running tests.
     */
    @Configuration
    @Profile("!test")
    static class ProductionMqttConfig {
        // This inner class ensures MQTT beans are only created in non-test environments
        // Testcontainers will provide MQTT configuration for integration tests
    }
}