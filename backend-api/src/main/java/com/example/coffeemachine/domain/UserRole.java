package com.example.coffeemachine.domain;

/**
 * Enumeration representing user roles in the coffee machine monitoring system.
 * 
 * Defines access control levels:
 * - FACILITY: Limited access to assigned facility's machines
 * - ADMIN: Full system access including facility management
 */
public enum UserRole {
    /**
     * Facility user - can monitor coffee machines in their assigned building
     * and perform basic machine operations
     */
    FACILITY,
    
    /**
     * Administrator - can manage all facilities, assign machines to facilities,
     * view aggregated data, and perform all system operations
     */
    ADMIN
}