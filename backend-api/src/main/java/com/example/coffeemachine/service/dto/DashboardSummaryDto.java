package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for dashboard summary information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    
    /**
     * Timestamp when the summary was generated.
     */
    private LocalDateTime timestamp;
    
    /**
     * Total number of facilities.
     */
    private int totalFacilities;
    
    /**
     * Total number of machines across all facilities.
     */
    private int totalMachines;
    
    /**
     * Number of machines currently online.
     */
    private int onlineMachines;
    
    /**
     * Number of machines currently offline.
     */
    private int offlineMachines;
    
    /**
     * Number of machines in error state.
     */
    private int errorMachines;
    
    /**
     * Total number of unresolved alerts.
     */
    private int totalUnresolvedAlerts;
    
    /**
     * Number of critical unresolved alerts.
     */
    private int criticalAlerts;
    
    /**
     * Number of warning unresolved alerts.
     */
    private int warningAlerts;
    
    /**
     * Recent alerts (last 24 hours).
     */
    private List<AlertDto> recentAlerts;
    
    /**
     * Facility statistics breakdown.
     */
    private List<FacilityStatisticsDto> facilityStatistics;
    
    /**
     * Calculates the overall machine health percentage.
     *
     * @return health percentage (0-100)
     */
    public double getOverallHealthPercentage() {
        if (totalMachines == 0) {
            return 100.0;
        }
        return (double) (totalMachines - errorMachines) / totalMachines * 100.0;
    }
    
    /**
     * Checks if there are any critical issues requiring attention.
     *
     * @return true if there are critical alerts or error machines
     */
    public boolean hasCriticalIssues() {
        return criticalAlerts > 0 || errorMachines > 0;
    }
}