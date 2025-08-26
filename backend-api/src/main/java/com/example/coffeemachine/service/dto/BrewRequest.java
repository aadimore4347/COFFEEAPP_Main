package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.BrewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for brew requests (to command a machine to make coffee).
 */
@Data
public class BrewRequest {
    
    @NotNull(message = "Brew type is required")
    private BrewType brewType;
    
    @Min(value = 10, message = "Volume must be at least 10ml")
    @Max(value = 500, message = "Volume cannot exceed 500ml")
    private Integer volumeMl;
}