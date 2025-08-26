package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.MachineStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for coffee machine information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeMachineDto {
    
    private Long id;
    private Long facilityId;
    private String facilityName;
    private MachineStatus status;
    private Double temperature;
    private Integer waterLevel;
    private Integer milkLevel;
    private Integer beansLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Recent usage history (optional, included when needed).
     */
    private List<UsageHistoryDto> recentUsage;
    
    /**
     * Active alerts for this machine (optional, included when needed).
     */
    private List<AlertDto> activeAlerts;
}