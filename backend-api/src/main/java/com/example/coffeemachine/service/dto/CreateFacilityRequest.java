package com.example.coffeemachine.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating new facilities.
 */
@Data
public class CreateFacilityRequest {
    
    @NotBlank(message = "Facility name is required")
    @Size(min = 2, max = 100, message = "Facility name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 255, message = "Location must be between 2 and 255 characters")
    private String location;
}