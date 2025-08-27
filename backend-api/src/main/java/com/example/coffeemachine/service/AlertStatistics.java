package com.example.coffeemachine.service;

import lombok.Builder;
import lombok.Data;

/**
 * Data class representing alert statistics.
 * Used for dashboard and reporting purposes.
 */
@Data
@Builder
public class AlertStatistics {
    
    /**
     * The facility ID (null for overall statistics).
     */
    private final Long facilityId;
    
    /**
     * Total number of alerts.
     */
    private final int totalAlerts;
    
    /**
     * Number of unresolved alerts.
     */
    private final int unresolvedAlerts;
    
    /**
     * Number of critical unresolved alerts.
     */
    private final int criticalAlerts;
    
    /**
     * Number of warning unresolved alerts.
     */
    private final int warningAlerts;
    
    /**
     * Number of info unresolved alerts.
     */
    private final int infoAlerts;
    
    /**
     * Calculates the percentage of alerts that are resolved.
     *
     * @return resolved percentage (0-100)
     */
    public double getResolvedPercentage() {
        if (totalAlerts == 0) {
            return 100.0;
        }
        return (double) (totalAlerts - unresolvedAlerts) / totalAlerts * 100.0;
    }
    
    /**
     * Calculates the percentage of unresolved alerts that are critical.
     *
     * @return critical percentage (0-100)
     */
    public double getCriticalPercentage() {
        if (unresolvedAlerts == 0) {
            return 0.0;
        }
        return (double) criticalAlerts / unresolvedAlerts * 100.0;
    }
    
    /**
     * Checks if there are any unresolved alerts.
     *
     * @return true if there are unresolved alerts
     */
    public boolean hasUnresolvedAlerts() {
        return unresolvedAlerts > 0;
    }
    
    /**
     * Checks if there are any critical alerts.
     *
     * @return true if there are critical alerts
     */
    public boolean hasCriticalAlerts() {
        return criticalAlerts > 0;
    }
    
    /**
     * Gets the total number of unresolved alerts by severity.
     *
     * @return total unresolved alerts
     */
    public int getTotalUnresolvedBySeverity() {
        return criticalAlerts + warningAlerts + infoAlerts;
    }
    
    /**
     * Checks if all alerts are resolved.
     *
     * @return true if all alerts are resolved
     */
    public boolean isAllResolved() {
        return unresolvedAlerts == 0;
    }
}