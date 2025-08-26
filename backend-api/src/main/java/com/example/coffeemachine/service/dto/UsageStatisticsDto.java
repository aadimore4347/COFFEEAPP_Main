package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.BrewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for usage statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatisticsDto {
    
    /**
     * The facility ID (null for overall statistics).
     */
    private Long facilityId;
    
    /**
     * The machine ID (null for facility/overall statistics).
     */
    private Long machineId;
    
    /**
     * Statistics period start time.
     */
    private LocalDateTime periodStart;
    
    /**
     * Statistics period end time.
     */
    private LocalDateTime periodEnd;
    
    /**
     * Total number of brews in the period.
     */
    private int totalBrews;
    
    /**
     * Total volume brewed in milliliters.
     */
    private int totalVolumeMl;
    
    /**
     * Average volume per brew in milliliters.
     */
    private double averageVolumeMl;
    
    /**
     * Breakdown of brews by type.
     */
    private Map<BrewType, Integer> brewsByType;
    
    /**
     * Breakdown of volume by type.
     */
    private Map<BrewType, Integer> volumeByType;
    
    /**
     * Peak usage hour (0-23).
     */
    private Integer peakHour;
    
    /**
     * Number of brews during peak hour.
     */
    private Integer peakHourBrews;
    
    /**
     * Most popular brew type.
     */
    private BrewType mostPopularBrewType;
    
    /**
     * Number of brews for the most popular type.
     */
    private Integer mostPopularBrewTypeCount;
}