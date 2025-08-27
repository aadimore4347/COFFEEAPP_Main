package com.example.coffeemachine.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper.
 *
 * @param <T> the type of data being returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Indicates if the request was successful.
     */
    private boolean success;
    
    /**
     * Human-readable message about the result.
     */
    private String message;
    
    /**
     * The actual data being returned (null for error responses).
     */
    private T data;
    
    /**
     * Error details (null for successful responses).
     */
    private ErrorDetails error;
    
    /**
     * Timestamp of the response.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Creates a successful response with data.
     *
     * @param data the response data
     * @param message the success message
     * @param <T> the type of data
     * @return successful API response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a successful response with data and default message.
     *
     * @param data the response data
     * @param <T> the type of data
     * @return successful API response
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Request completed successfully");
    }
    
    /**
     * Creates an error response.
     *
     * @param message the error message
     * @param errorCode the error code
     * @param <T> the type of data
     * @return error API response
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetails.builder()
                        .code(errorCode)
                        .message(message)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response with default error code.
     *
     * @param message the error message
     * @param <T> the type of data
     * @return error API response
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, "GENERAL_ERROR");
    }
}