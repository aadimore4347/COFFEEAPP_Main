package com.example.coffeemachine.service;

import lombok.Builder;
import lombok.Data;

/**
 * Data class representing facility statistics.
 * Used for dashboard and reporting purposes.
 */
@Data
@Builder
public class FacilityStatistics {
    
    /**
     * The facility ID.
     */
    private final Long facilityId;
    
    /**
     * The facility name.
     */
    private final String facilityName;
    
    /**
     * Total number of machines in the facility.
     */
    private final int totalMachines;
    
    /**
     * Number of machines currently online (ON status).
     */
    private final int onlineMachines;
    
    /**
     * Number of machines currently offline (OFF status).
     */
    private final int offlineMachines;
    
    /**
     * Number of machines in error state.
     */
    private final int errorMachines;
    
    /**
     * Calculates the percentage of machines that are online.
     *
     * @return online percentage (0-100)
     */
    public double getOnlinePercentage() {
        if (totalMachines == 0) {
            return 0.0;
        }
        return (double) onlineMachines / totalMachines * 100.0;
    }
    
    /**
     * Calculates the percentage of machines that are in error state.
     *
     * @return error percentage (0-100)
     */
    public double getErrorPercentage() {
        if (totalMachines == 0) {
            return 0.0;
        }
        return (double) errorMachines / totalMachines * 100.0;
    }
    
    /**
     * Checks if the facility has any machines in error state.
     *
     * @return true if there are error machines
     */
    public boolean hasErrors() {
        return errorMachines > 0;
    }
    
    /**
     * Checks if all machines are online.
     *
     * @return true if all machines are online
     */
    public boolean isAllOnline() {
        return totalMachines > 0 && onlineMachines == totalMachines;
    }
}