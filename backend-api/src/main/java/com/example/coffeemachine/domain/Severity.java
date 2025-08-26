package com.example.coffeemachine.domain;

/**
 * Enumeration representing the severity levels of alerts.
 * 
 * Used to categorize alerts by their importance and urgency,
 * helping prioritize response actions.
 */
public enum Severity {
    /**
     * Informational alert - no immediate action required
     */
    INFO,
    
    /**
     * Warning alert - attention recommended but not critical
     */
    WARNING,
    
    /**
     * Critical alert - immediate attention required
     */
    CRITICAL
}