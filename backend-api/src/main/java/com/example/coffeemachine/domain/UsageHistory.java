package com.example.coffeemachine.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entity representing a usage history record for coffee machine brewing activities.
 * 
 * Tracks individual brewing events with details about the beverage type,
 * volume, temperature, and timing for analytics and monitoring purposes.
 */
@Entity
@Table(name = "usage_history",
       indexes = {
           @Index(name = "idx_usage_machine", columnList = "machine_id"),
           @Index(name = "idx_usage_timestamp", columnList = "timestamp"),
           @Index(name = "idx_usage_brew_type", columnList = "brew_type"),
           @Index(name = "idx_usage_machine_timestamp", columnList = "machine_id, timestamp")
       })
public class UsageHistory extends BaseEntity {

    /**
     * The coffee machine that performed this brewing operation.
     * Required relationship - every usage record must belong to a machine.
     */
    @NotNull(message = "Usage history must belong to a coffee machine")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", 
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_usage_machine"))
    private CoffeeMachine machine;

    /**
     * Timestamp when the brewing operation occurred.
     * Received via MQTT usage messages or recorded during API-triggered brews.
     */
    @NotNull(message = "Usage timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Type of beverage that was brewed.
     * Determines the recipe and expected volume/ingredients.
     */
    @NotNull(message = "Brew type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "brew_type", nullable = false, length = 20)
    private BrewType brewType;

    /**
     * Volume of the brewed beverage in milliliters.
     * Used for consumption tracking and supply calculations.
     */
    @Positive(message = "Volume must be positive")
    @Max(value = 500, message = "Volume cannot exceed 500ml")
    @Column(name = "volume_ml")
    private Integer volumeMl;

    /**
     * Brewing temperature at the time of operation in Celsius.
     * Captured for quality assurance and optimal brewing analysis.
     */
    @DecimalMin(value = "70.0", message = "Brewing temperature must be at least 70°C")
    @DecimalMax(value = "100.0", message = "Brewing temperature cannot exceed 100°C")
    @Column(name = "temp_at_brew", precision = 5, scale = 2)
    private BigDecimal tempAtBrew;

    /**
     * Constructor for creating a new usage history record.
     */
    public UsageHistory() {
    }

    public UsageHistory(CoffeeMachine machine, LocalDateTime timestamp, BrewType brewType) {
        this.machine = machine;
        this.timestamp = timestamp;
        this.brewType = brewType;
    }

    /**
     * Constructor with all details.
     */
    public UsageHistory(CoffeeMachine machine, LocalDateTime timestamp, 
                       BrewType brewType, Integer volumeMl, Double tempAtBrew) {
        this.machine = machine;
        this.timestamp = timestamp;
        this.brewType = brewType;
        this.volumeMl = volumeMl;
        this.tempAtBrew = tempAtBrew != null ? new BigDecimal(tempAtBrew.toString()) : null;
    }

    /**
     * Get the facility ID through the machine relationship.
     * Useful for facility-level analytics.
     */
    public Long getFacilityId() {
        return machine != null && machine.getFacility() != null ? 
               machine.getFacility().getId() : null;
    }

    /**
     * Get the facility name through the machine relationship.
     */
    public String getFacilityName() {
        return machine != null && machine.getFacility() != null ? 
               machine.getFacility().getName() : null;
    }

    /**
     * Check if this usage occurred within the specified hours ago.
     */
    public boolean isRecentUsage(int hoursAgo) {
        return timestamp != null && 
               timestamp.isAfter(LocalDateTime.now().minusHours(hoursAgo));
    }

    /**
     * Check if this was a peak hours usage (typically 7-9 AM and 1-3 PM).
     */
    public boolean isPeakHoursUsage() {
        if (timestamp == null) return false;
        
        int hour = timestamp.getHour();
        return (hour >= 7 && hour <= 9) || (hour >= 13 && hour <= 15);
    }

    /**
     * Get expected volume for the brew type if not explicitly set.
     */
    public Integer getExpectedVolume() {
        if (volumeMl != null) {
            return volumeMl;
        }
        
        // Return typical volumes based on brew type
        return switch (brewType) {
            case ESPRESSO -> 30;
            case DOUBLE_ESPRESSO -> 60;
            case MACCHIATO -> 40;
            case CAPPUCCINO -> 150;
            case FLAT_WHITE -> 160;
            case LATTE, MOCHA -> 200;
            case AMERICANO -> 250;
        };
    }

    /**
     * Check if the brewing temperature was optimal for the beverage type.
     */
    public boolean isOptimalTemperature() {
        if (tempAtBrew == null) return false;
        
        // Optimal temperature ranges for different brew types
        return switch (brewType) {
            case ESPRESSO, DOUBLE_ESPRESSO -> tempAtBrew.compareTo(BigDecimal.valueOf(88)) >= 0 && tempAtBrew.compareTo(BigDecimal.valueOf(94)) <= 0;
            case AMERICANO -> tempAtBrew.compareTo(BigDecimal.valueOf(85)) >= 0 && tempAtBrew.compareTo(BigDecimal.valueOf(92)) <= 0;
            case CAPPUCCINO, LATTE, MOCHA, FLAT_WHITE, MACCHIATO -> tempAtBrew.compareTo(BigDecimal.valueOf(86)) >= 0 && tempAtBrew.compareTo(BigDecimal.valueOf(93)) <= 0;
        };
    }

    /**
     * Calculate approximate milk usage for this brew (in ml).
     */
    public Integer getMilkUsage() {
        return switch (brewType) {
            case ESPRESSO, DOUBLE_ESPRESSO, AMERICANO -> 0;
            case MACCHIATO -> 10;
            case CAPPUCCINO -> 100;
            case FLAT_WHITE -> 130;
            case LATTE -> 150;
            case MOCHA -> 140;
        };
    }

    /**
     * Calculate approximate coffee beans usage for this brew (in grams).
     */
    public Integer getBeansUsage() {
        return switch (brewType) {
            case ESPRESSO -> 7;
            case DOUBLE_ESPRESSO -> 14;
            case MACCHIATO -> 7;
            case CAPPUCCINO, FLAT_WHITE -> 8;
            case LATTE, MOCHA -> 9;
            case AMERICANO -> 10;
        };
    }

    // Manual accessors
    public CoffeeMachine getMachine() { return machine; }
    public void setMachine(CoffeeMachine machine) { this.machine = machine; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public BrewType getBrewType() { return brewType; }
    public void setBrewType(BrewType brewType) { this.brewType = brewType; }
    public Integer getVolumeMl() { return volumeMl; }
    public void setVolumeMl(Integer volumeMl) { this.volumeMl = volumeMl; }
    public BigDecimal getTempAtBrew() { return tempAtBrew; }
    public void setTempAtBrew(BigDecimal tempAtBrew) { this.tempAtBrew = tempAtBrew; }
}