package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.MachineStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * DTO for creating new coffee machines.
 */
@Data
public class CreateMachineRequest {
    
    /**
     * Initial status of the machine (optional, defaults to OFF).
     */
    private MachineStatus status;
    
    /**
     * Initial temperature (optional, defaults to 0.0).
     */
    @Min(value = 0, message = "Temperature cannot be negative")
    @Max(value = 120, message = "Temperature cannot exceed 120°C")
    private Double temperature;
    
    /**
     * Initial water level percentage (optional, defaults to 100).
     */
    @Min(value = 0, message = "Water level cannot be negative")
    @Max(value = 100, message = "Water level cannot exceed 100%")
    private Integer waterLevel;
    
    /**
     * Initial milk level percentage (optional, defaults to 100).
     */
    @Min(value = 0, message = "Milk level cannot be negative")
    @Max(value = 100, message = "Milk level cannot exceed 100%")
    private Integer milkLevel;
    
    /**
     * Initial beans level percentage (optional, defaults to 100).
     */
    @Min(value = 0, message = "Beans level cannot be negative")
    @Max(value = 100, message = "Beans level cannot exceed 100%")
    private Integer beansLevel;
}