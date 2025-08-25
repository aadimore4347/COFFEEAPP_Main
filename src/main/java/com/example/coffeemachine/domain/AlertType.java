package com.example.coffeemachine.domain;

/**
 * Enumeration representing different types of alerts that can be generated
 * by the coffee machine monitoring system.
 * 
 * Each type corresponds to specific threshold conditions or machine states.
 */
public enum AlertType {
    /**
     * Water level has fallen below the configured threshold (default: 20%)
     */
    LOW_WATER,
    
    /**
     * Milk level has fallen below the configured threshold (default: 20%)
     */
    LOW_MILK,
    
    /**
     * Coffee beans level has fallen below the configured threshold (default: 20%)
     */
    LOW_BEANS,
    
    /**
     * Machine has entered ERROR status indicating a malfunction
     */
    MALFUNCTION
}