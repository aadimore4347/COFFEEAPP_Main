package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for facility statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityStatisticsDto {
    
    private Long facilityId;
    private String facilityName;
    private int totalMachines;
    private int onlineMachines;
    private int offlineMachines;
    private int errorMachines;
    private double onlinePercentage;
    private double errorPercentage;
    private boolean hasErrors;
    private boolean isAllOnline;
}