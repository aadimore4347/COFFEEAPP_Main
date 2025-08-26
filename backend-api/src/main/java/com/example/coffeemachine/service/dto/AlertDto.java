package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.AlertType;
import com.example.coffeemachine.domain.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for alert information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {
    
    private Long id;
    private Long machineId;
    private String machineFacilityName;
    private AlertType type;
    private Severity severity;
    private String message;
    private Integer threshold;
    private boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}