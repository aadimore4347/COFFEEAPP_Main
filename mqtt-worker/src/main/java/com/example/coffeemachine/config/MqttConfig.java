package com.example.coffeemachine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MqttConfig {

    private static final Logger log = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${spring.mqtt.broker.url}")
    private String brokerUrl;

    @Value("${spring.mqtt.broker.client-id}")
    private String clientId;

    @Value("${spring.mqtt.broker.username:}")
    private String username;

    @Value("${spring.mqtt.broker.password:}")
    private String password;

    private final com.example.coffeemachine.mqtt.MqttMessageHandler mqttMessageHandler;

    public MqttConfig(com.example.coffeemachine.mqtt.MqttMessageHandler mqttMessageHandler) {
        this.mqttMessageHandler = mqttMessageHandler;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        options.setServerURIs(new String[]{brokerUrl});
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        
        if (username != null && !username.isEmpty()) {
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }
        
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = 
            new MqttPahoMessageDrivenChannelAdapter(
                clientId + "-inbound", 
                mqttClientFactory(),
                "coffeeMachine/+/temperature",
                "coffeeMachine/+/waterLevel",
                "coffeeMachine/+/milkLevel",
                "coffeeMachine/+/beansLevel",
                "coffeeMachine/+/status",
                "coffeeMachine/+/usage"
            );

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_topic").toString();
            String payload = new String((byte[]) message.getPayload());
            
            log.info("Received MQTT message on topic: {} with payload: {}", topic, payload);
            
            // Extract machine ID from topic (e.g., coffeeMachine/123/temperature -> 123)
            String[] topicParts = topic.split("/");
            if (topicParts.length >= 3) {
                String machineId = topicParts[1];
                String metricType = topicParts[2];
                
                // Route to appropriate handler based on metric type
                try {
                    switch (metricType) {
                        case "temperature" -> mqttMessageHandler.handleTemperatureUpdate(machineId, payload);
                        case "waterLevel" -> mqttMessageHandler.handleWaterLevelUpdate(machineId, payload);
                        case "milkLevel" -> mqttMessageHandler.handleMilkLevelUpdate(machineId, payload);
                        case "beansLevel" -> mqttMessageHandler.handleBeansLevelUpdate(machineId, payload);
                        case "status" -> mqttMessageHandler.handleStatusUpdate(machineId, payload);
                        case "usage" -> mqttMessageHandler.handleUsageEvent(machineId, payload);
                        default -> log.debug("Unknown metric type {} for topic {}", metricType, topic);
                    }
                } catch (Exception ex) {
                    log.warn("Failed to route message for machine {} metric {}: {}", machineId, metricType, ex.getMessage());
                }
            }
        };
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    // routing is handled inline in handler()
}