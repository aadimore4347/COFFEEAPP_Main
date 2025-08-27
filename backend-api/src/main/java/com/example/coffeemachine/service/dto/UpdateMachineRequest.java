package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.MachineStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * DTO for updating coffee machine information.
 */
@Data
public class UpdateMachineRequest {
    
    /**
     * Machine status.
     */
    private MachineStatus status;
    
    /**
     * Machine temperature.
     */
    @Min(value = 0, message = "Temperature cannot be negative")
    @Max(value = 120, message = "Temperature cannot exceed 120°C")
    private Double temperature;
    
    /**
     * Water level percentage.
     */
    @Min(value = 0, message = "Water level cannot be negative")
    @Max(value = 100, message = "Water level cannot exceed 100%")
    private Integer waterLevel;
    
    /**
     * Milk level percentage.
     */
    @Min(value = 0, message = "Milk level cannot be negative")
    @Max(value = 100, message = "Milk level cannot exceed 100%")
    private Integer milkLevel;
    
    /**
     * Beans level percentage.
     */
    @Min(value = 0, message = "Beans level cannot be negative")
    @Max(value = 100, message = "Beans level cannot exceed 100%")
    private Integer beansLevel;
}