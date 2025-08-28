package com.example.coffeemachine.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple health check controller for testing basic endpoint functionality.
 */
@RestController
@RequestMapping("/api/health")
@Slf4j
@Tag(name = "Health Check", description = "Basic health check endpoints")
public class HealthController {

    /**
     * Basic health check endpoint.
     *
     * @return health status
     */
    @GetMapping
    @Operation(summary = "Basic health check", description = "Check if the application is running")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("Health check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Coffee Machine Monitoring System");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check endpoint.
     *
     * @return detailed health information
     */
    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check", description = "Get detailed health information")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        log.debug("Detailed health check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Coffee Machine Monitoring System");
        response.put("version", "1.0.0");
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("osName", System.getProperty("os.name"));
        response.put("osVersion", System.getProperty("os.version"));
        response.put("memory", new HashMap<String, Object>() {{
            put("total", Runtime.getRuntime().totalMemory());
            put("free", Runtime.getRuntime().freeMemory());
            put("used", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        }});
        
        return ResponseEntity.ok(response);
    }
}