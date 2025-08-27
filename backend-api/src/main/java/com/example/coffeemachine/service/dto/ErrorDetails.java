package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for error details in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    
    /**
     * Error code for programmatic handling.
     */
    private String code;
    
    /**
     * Human-readable error message.
     */
    private String message;
    
    /**
     * Additional error details or field-specific errors.
     */
    private List<String> details;
    
    /**
     * Creates error details with just code and message.
     *
     * @param code the error code
     * @param message the error message
     * @return error details
     */
    public static ErrorDetails of(String code, String message) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .build();
    }
    
    /**
     * Creates error details with code, message, and additional details.
     *
     * @param code the error code
     * @param message the error message
     * @param details additional error details
     * @return error details
     */
    public static ErrorDetails of(String code, String message, List<String> details) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }
}