package com.example.coffeemachine.alert;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration class for alert thresholds.
 * In future versions, these could be loaded from configuration files
 * or made configurable per facility/machine.
 */
@Data
@Builder
public class AlertThresholds {
    
    /**
     * Water level threshold percentage (0-100) below which low water alert is triggered.
     */
    private final int lowWaterThreshold;
    
    /**
     * Milk level threshold percentage (0-100) below which low milk alert is triggered.
     */
    private final int lowMilkThreshold;
    
    /**
     * Beans level threshold percentage (0-100) below which low beans alert is triggered.
     */
    private final int lowBeansThreshold;
    
    /**
     * Minimum acceptable temperature in Celsius.
     */
    private final double minTemperature;
    
    /**
     * Maximum acceptable temperature in Celsius.
     */
    private final double maxTemperature;
    
    /**
     * Creates default alert thresholds.
     *
     * @return default alert thresholds
     */
    public static AlertThresholds defaultThresholds() {
        return AlertThresholds.builder()
                .lowWaterThreshold(20)
                .lowMilkThreshold(20)
                .lowBeansThreshold(20)
                .minTemperature(85.0)
                .maxTemperature(100.0)
                .build();
    }
}