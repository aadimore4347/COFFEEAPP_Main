package com.example.coffeemachine.alert;

import com.example.coffeemachine.domain.*;
import com.example.coffeemachine.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for evaluating machine conditions and generating alerts.
 * Implements threshold-based alerting with debouncing to prevent spam.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertEvaluatorService {

    private final AlertRepository alertRepository;
    private final AlertNotificationService alertNotificationService;
    
    // Alert thresholds (configurable via properties in future)
    private static final int LOW_WATER_THRESHOLD = 20;
    private static final int LOW_MILK_THRESHOLD = 20;
    private static final int LOW_BEANS_THRESHOLD = 20;
    private static final double MIN_TEMPERATURE = 85.0;
    private static final double MAX_TEMPERATURE = 100.0;
    
    /**
     * Evaluates water level and creates/resolves alerts as needed.
     *
     * @param machine the coffee machine to evaluate
     */
    public void evaluateWaterLevelAlerts(CoffeeMachine machine) {
        if (machine.getWaterLevel() == null) {
            return;
        }
        
        if (machine.getWaterLevel() < LOW_WATER_THRESHOLD) {
            createAlertIfNotExists(machine, AlertType.LOW_WATER, 
                    String.format("Water level is low: %d%% (threshold: %d%%)", 
                            machine.getWaterLevel(), LOW_WATER_THRESHOLD),
                    Severity.WARNING, LOW_WATER_THRESHOLD);
        } else {
            // Resolve existing low water alerts if water level is above threshold
            resolveAlertsIfExists(machine, AlertType.LOW_WATER);
        }
    }
    
    /**
     * Evaluates milk level and creates/resolves alerts as needed.
     *
     * @param machine the coffee machine to evaluate
     */
    public void evaluateMilkLevelAlerts(CoffeeMachine machine) {
        if (machine.getMilkLevel() == null) {
            return;
        }
        
        if (machine.getMilkLevel() < LOW_MILK_THRESHOLD) {
            createAlertIfNotExists(machine, AlertType.LOW_MILK,
                    String.format("Milk level is low: %d%% (threshold: %d%%)", 
                            machine.getMilkLevel(), LOW_MILK_THRESHOLD),
                    Severity.WARNING, LOW_MILK_THRESHOLD);
        } else {
            resolveAlertsIfExists(machine, AlertType.LOW_MILK);
        }
    }
    
    /**
     * Evaluates beans level and creates/resolves alerts as needed.
     *
     * @param machine the coffee machine to evaluate
     */
    public void evaluateBeansLevelAlerts(CoffeeMachine machine) {
        if (machine.getBeansLevel() == null) {
            return;
        }
        
        if (machine.getBeansLevel() < LOW_BEANS_THRESHOLD) {
            createAlertIfNotExists(machine, AlertType.LOW_BEANS,
                    String.format("Beans level is low: %d%% (threshold: %d%%)", 
                            machine.getBeansLevel(), LOW_BEANS_THRESHOLD),
                    Severity.WARNING, LOW_BEANS_THRESHOLD);
        } else {
            resolveAlertsIfExists(machine, AlertType.LOW_BEANS);
        }
    }
    
    /**
     * Evaluates temperature and creates alerts for extreme values.
     *
     * @param machine the coffee machine to evaluate
     */
    public void evaluateTemperatureAlerts(CoffeeMachine machine) {
        if (machine.getTemperature() == null) {
            return;
        }
        
        double temp = machine.getTemperature();
        
        if (temp < MIN_TEMPERATURE || temp > MAX_TEMPERATURE) {
            String message = temp < MIN_TEMPERATURE ? 
                    String.format("Temperature too low: %.1f°C (min: %.1f°C)", temp, MIN_TEMPERATURE) :
                    String.format("Temperature too high: %.1f°C (max: %.1f°C)", temp, MAX_TEMPERATURE);
            
            Severity severity = (temp < MIN_TEMPERATURE - 10 || temp > MAX_TEMPERATURE + 10) ? 
                    Severity.CRITICAL : Severity.WARNING;
            
            createAlertIfNotExists(machine, AlertType.MALFUNCTION, message, severity, (int) temp);
        }
        // Note: We don't auto-resolve temperature alerts as they might indicate equipment issues
    }
    
    /**
     * Evaluates machine status and creates malfunction alerts.
     *
     * @param machine the coffee machine to evaluate
     * @param previousStatus the previous status for comparison
     */
    public void evaluateStatusAlerts(CoffeeMachine machine, MachineStatus previousStatus) {
        if (machine.getStatus() == MachineStatus.ERROR) {
            createAlertIfNotExists(machine, AlertType.MALFUNCTION,
                    String.format("Machine is in ERROR state (was: %s)", 
                            previousStatus != null ? previousStatus : "UNKNOWN"),
                    Severity.CRITICAL, 0);
        } else if (previousStatus == MachineStatus.ERROR && 
                   (machine.getStatus() == MachineStatus.ON || machine.getStatus() == MachineStatus.OFF)) {
            // Machine recovered from error state
            resolveAlertsIfExists(machine, AlertType.MALFUNCTION);
            log.info("Machine {} recovered from ERROR state to {}", machine.getId(), machine.getStatus());
        }
    }
    
    /**
     * Creates an alert if one doesn't already exist for the same machine and type.
     * Implements debouncing to prevent alert spam.
     *
     * @param machine the machine
     * @param type the alert type
     * @param message the alert message
     * @param severity the alert severity
     * @param threshold the threshold value that triggered the alert
     */
    private void createAlertIfNotExists(CoffeeMachine machine, AlertType type, String message, 
                                      Severity severity, Integer threshold) {
        
        // Check if there's already an unresolved alert of this type
        Optional<Alert> existingAlert = alertRepository
                .findMostRecentUnresolvedByMachineIdAndType(machine.getId(), type);
        
        if (existingAlert.isEmpty()) {
            Alert alert = Alert.builder()
                    .machine(machine)
                    .type(type)
                    .severity(severity)
                    .message(message)
                    .threshold(threshold)
                    .resolved(false)
                    .build();
            
            Alert saved = alertRepository.save(alert);
            log.warn("Created alert for machine {}: {} - {}", machine.getId(), type, message);
            
            // Send notification
            alertNotificationService.sendAlertNotification(saved);
        } else {
            log.debug("Alert already exists for machine {} and type {}, skipping", machine.getId(), type);
        }
    }
    
    /**
     * Resolves existing unresolved alerts of the specified type for the machine.
     *
     * @param machine the machine
     * @param type the alert type to resolve
     */
    private void resolveAlertsIfExists(CoffeeMachine machine, AlertType type) {
        int resolvedCount = alertRepository.resolveAlertsByMachineIdAndType(machine.getId(), type);
        if (resolvedCount > 0) {
            log.info("Resolved {} {} alerts for machine {}", resolvedCount, type, machine.getId());
        }
    }
    
    /**
     * Manually resolves a specific alert by ID.
     *
     * @param alertId the alert ID
     * @return true if alert was resolved, false if not found
     */
    public boolean resolveAlert(Long alertId) {
        int resolved = alertRepository.resolveAlertById(alertId);
        if (resolved > 0) {
            log.info("Manually resolved alert {}", alertId);
            return true;
        }
        return false;
    }
    
    /**
     * Gets the current alert thresholds.
     *
     * @return alert thresholds configuration
     */
    public AlertThresholds getThresholds() {
        return AlertThresholds.builder()
                .lowWaterThreshold(LOW_WATER_THRESHOLD)
                .lowMilkThreshold(LOW_MILK_THRESHOLD)
                .lowBeansThreshold(LOW_BEANS_THRESHOLD)
                .minTemperature(MIN_TEMPERATURE)
                .maxTemperature(MAX_TEMPERATURE)
                .build();
    }
}