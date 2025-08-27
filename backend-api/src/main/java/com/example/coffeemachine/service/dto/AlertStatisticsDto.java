package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for alert statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertStatisticsDto {
    
    private Long facilityId;
    private int totalAlerts;
    private int unresolvedAlerts;
    private int criticalAlerts;
    private int warningAlerts;
    private int infoAlerts;
    private double resolvedPercentage;
    private double criticalPercentage;
    private boolean hasUnresolvedAlerts;
    private boolean hasCriticalAlerts;
}