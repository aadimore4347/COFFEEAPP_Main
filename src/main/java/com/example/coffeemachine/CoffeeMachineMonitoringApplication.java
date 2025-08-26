package com.example.coffeemachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Coffee Machine Monitoring System.
 * 
 * This application provides:
 * - Real-time MQTT monitoring of coffee machines
 * - REST APIs for facility and admin management
 * - JWT-based authentication and authorization
 * - Alert system for machine maintenance
 * - Role-based dashboards for facilities and administrators
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@IntegrationComponentScan
public class CoffeeMachineMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeMachineMonitoringApplication.class, args);
    }
}