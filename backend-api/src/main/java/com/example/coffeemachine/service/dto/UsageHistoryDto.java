package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.BrewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for usage history information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageHistoryDto {
    
    private Long id;
    private Long machineId;
    private LocalDateTime timestamp;
    private BrewType brewType;
    private Integer volumeMl;
    private Double tempAtBrew;
}