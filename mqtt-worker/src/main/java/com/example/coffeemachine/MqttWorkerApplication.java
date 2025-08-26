package com.example.coffeemachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqttWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttWorkerApplication.class, args);
    }
}