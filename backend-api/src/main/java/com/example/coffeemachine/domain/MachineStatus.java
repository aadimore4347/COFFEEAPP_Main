package com.example.coffeemachine.domain;

/**
 * Enumeration representing the operational status of a coffee machine.
 * 
 * Used to track real-time machine state received via MQTT.
 */
public enum MachineStatus {
    /**
     * Machine is powered on and operational
     */
    ON,
    
    /**
     * Machine is powered off or in standby mode
     */
    OFF,
    
    /**
     * Machine has encountered an error and requires attention
     */
    ERROR
}