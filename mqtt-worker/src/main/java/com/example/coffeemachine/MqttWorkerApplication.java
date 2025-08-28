package com.example.coffeemachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MqttWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttWorkerApplication.class, args);
    }
}