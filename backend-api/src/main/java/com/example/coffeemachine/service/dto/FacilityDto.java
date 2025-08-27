package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for facility information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {
    
    private Long id;
    private String name;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * List of coffee machines in this facility (optional, included when needed).
     */
    private List<CoffeeMachineDto> coffeeMachines;
}