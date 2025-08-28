package com.example.coffeemachine.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
@EnableScheduling
@Profile("dev")
public class MqttPublisherSimulator {
    private static final Logger log = LoggerFactory.getLogger(MqttPublisherSimulator.class);

    @Value("${spring.mqtt.broker.url}")
    private String brokerUrl;

    @Value("${spring.mqtt.broker.username:}")
    private String username;

    @Value("${spring.mqtt.broker.password:}")
    private String password;

    private final Random random = new Random();

    private int waterLevel = 100;
    private int milkLevel = 100;
    private int beansLevel = 100;
    private int machineId = 1;
    private int decrementTick = 0;

    @Scheduled(fixedDelay = 2000)
    public void publishDecreasingLevels() {
        try (MqttClient client = new MqttClient(brokerUrl, "sim-publisher-" + System.nanoTime())) {
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            if (username != null && !username.isEmpty()) {
                opts.setUserName(username);
                opts.setPassword(password.toCharArray());
            }
            client.connect(opts);

            // Decrease a different resource every tick; wrap around and clamp >= 0
            decrementTick = (decrementTick + 1) % 3;
            if (decrementTick == 0 && waterLevel > 0) waterLevel -= random.nextInt(5) + 1;
            if (decrementTick == 1 && milkLevel > 0) milkLevel -= random.nextInt(5) + 1;
            if (decrementTick == 2 && beansLevel > 0) beansLevel -= random.nextInt(5) + 1;
            waterLevel = Math.max(0, waterLevel);
            milkLevel = Math.max(0, milkLevel);
            beansLevel = Math.max(0, beansLevel);

            publish(client, String.format("coffeeMachine/%d/waterLevel", machineId), Integer.toString(waterLevel));
            publish(client, String.format("coffeeMachine/%d/milkLevel", machineId), Integer.toString(milkLevel));
            publish(client, String.format("coffeeMachine/%d/beansLevel", machineId), Integer.toString(beansLevel));

            // Also publish status and temperature occasionally
            int temp = 80 + random.nextInt(15);
            publish(client, String.format("coffeeMachine/%d/temperature", machineId), Integer.toString(temp));
            publish(client, String.format("coffeeMachine/%d/status", machineId), random.nextBoolean() ? "READY" : "BREWING");

            client.disconnect();
        } catch (Exception e) {
            log.warn("MQTT simulator publish failed: {}", e.getMessage());
        }
    }

    private void publish(MqttClient client, String topic, String payload) throws Exception {
        MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        msg.setQos(0);
        client.publish(topic, msg);
    }
}