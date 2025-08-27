package com.example.coffeemachine.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Entity representing a coffee machine in the monitoring system.
 * 
 * Tracks real-time status, supply levels, and operational data
 * received via MQTT and managed through REST APIs.
 */
@Entity
@Table(name = "coffee_machine",
       indexes = {
           @Index(name = "idx_machine_facility", columnList = "facility_id"),
           @Index(name = "idx_machine_status", columnList = "status"),
           @Index(name = "idx_machine_levels", columnList = "water_level, milk_level, beans_level")
       })
public class CoffeeMachine extends BaseEntity {

    /**
     * The facility where this coffee machine is located.
     * Required relationship - every machine must belong to a facility.
     */
    @NotNull(message = "Coffee machine must belong to a facility")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", 
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_machine_facility"))
    private Facility facility;

    /**
     * Current operational status of the machine.
     * Updated via MQTT status messages.
     */
    @NotNull(message = "Machine status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private MachineStatus status = MachineStatus.OFF;

    /**
     * Current brewing temperature in Celsius.
     * Updated via MQTT temperature messages.
     */
    @DecimalMin(value = "0.0", message = "Temperature cannot be negative")
    @DecimalMax(value = "150.0", message = "Temperature cannot exceed 150°C")
    @Column(name = "temperature", precision = 5, scale = 2)
    private BigDecimal temperature;

    /**
     * Current water level as percentage (0-100).
     * Updated via MQTT waterLevel messages.
     */
    @Min(value = 0, message = "Water level cannot be negative")
    @Max(value = 100, message = "Water level cannot exceed 100%")
    @Column(name = "water_level")
    private Integer waterLevel;

    /**
     * Current milk level as percentage (0-100).
     * Updated via MQTT milkLevel messages.
     */
    @Min(value = 0, message = "Milk level cannot be negative")
    @Max(value = 100, message = "Milk level cannot exceed 100%")
    @Column(name = "milk_level")
    private Integer milkLevel;

    /**
     * Current coffee beans level as percentage (0-100).
     * Updated via MQTT beansLevel messages.
     */
    @Min(value = 0, message = "Beans level cannot be negative")
    @Max(value = 100, message = "Beans level cannot exceed 100%")
    @Column(name = "beans_level")
    private Integer beansLevel;

    /**
     * Usage history records for this machine.
     * Tracks all brewing activities.
     */
    @OneToMany(mappedBy = "machine", 
               cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY,
               orphanRemoval = true)
    private List<UsageHistory> usageHistory = new ArrayList<>();

    /**
     * Alerts generated for this machine.
     * Tracks all alert conditions and resolutions.
     */
    @OneToMany(mappedBy = "machine", 
               cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY,
               orphanRemoval = true)
    private List<Alert> alerts = new ArrayList<>();

    /**
     * Constructor for creating a new coffee machine.
     */
    public CoffeeMachine() {
    }

    public CoffeeMachine(Facility facility) {
        this.facility = facility;
        this.status = MachineStatus.OFF;
    }

    /**
     * Constructor with initial values.
     */
    public CoffeeMachine(Facility facility, MachineStatus status, 
                        Double temperature, Integer waterLevel, 
                        Integer milkLevel, Integer beansLevel) {
        this.facility = facility;
        this.status = status;
        this.temperature = new BigDecimal(temperature.toString());
        this.waterLevel = waterLevel;
        this.milkLevel = milkLevel;
        this.beansLevel = beansLevel;
    }

    /**
     * Update machine status from MQTT message.
     */
    public void updateStatus(MachineStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Update temperature from MQTT message.
     */
    public void updateTemperature(Double newTemperature) {
        if (newTemperature != null && newTemperature >= 0 && newTemperature <= 150) {
            this.temperature = new BigDecimal(newTemperature.toString());
        }
    }

    /**
     * Update water level from MQTT message.
     */
    public void updateWaterLevel(Integer newLevel) {
        if (newLevel != null && newLevel >= 0 && newLevel <= 100) {
            this.waterLevel = newLevel;
        }
    }

    /**
     * Update milk level from MQTT message.
     */
    public void updateMilkLevel(Integer newLevel) {
        if (newLevel != null && newLevel >= 0 && newLevel <= 100) {
            this.milkLevel = newLevel;
        }
    }

    /**
     * Update beans level from MQTT message.
     */
    public void updateBeansLevel(Integer newLevel) {
        if (newLevel != null && newLevel >= 0 && newLevel <= 100) {
            this.beansLevel = newLevel;
        }
    }

    /**
     * Add usage history record.
     */
    public void addUsageHistory(UsageHistory usage) {
        if (usage != null) {
            usageHistory.add(usage);
            usage.setMachine(this);
        }
    }

    /**
     * Add alert for this machine.
     */
    public void addAlert(Alert alert) {
        if (alert != null) {
            alerts.add(alert);
            alert.setMachine(this);
        }
    }

    /**
     * Check if machine is operational (ON status and active).
     */
    public boolean isOperational() {
        return isActive() && status == MachineStatus.ON;
    }

    /**
     * Check if machine has any low supply levels based on thresholds.
     */
    public boolean hasLowSupplies(int waterThreshold, int milkThreshold, int beansThreshold) {
        return (waterLevel != null && waterLevel < waterThreshold) ||
               (milkLevel != null && milkLevel < milkThreshold) ||
               (beansLevel != null && beansLevel < beansThreshold);
    }

    /**
     * Check if machine needs attention (ERROR status or low supplies).
     */
    public boolean needsAttention(int waterThreshold, int milkThreshold, int beansThreshold) {
        return status == MachineStatus.ERROR || 
               hasLowSupplies(waterThreshold, milkThreshold, beansThreshold);
    }

    /**
     * Get count of unresolved alerts for this machine.
     */
    public long getUnresolvedAlertCount() {
        return alerts.stream()
                .filter(alert -> alert.isActive() && !alert.getResolved())
                .count();
    }

    /**
     * Get the most recent temperature reading.
     */
    public Double getCurrentTemperature() {
        return temperature.doubleValue();
    }

    /**
     * Check if all supply levels are within acceptable range.
     */
    public boolean hasAdequateSupplies(int threshold) {
        return (waterLevel == null || waterLevel >= threshold) &&
               (milkLevel == null || milkLevel >= threshold) &&
               (beansLevel == null || beansLevel >= threshold);
    }

    // Manual accessors
    public Facility getFacility() { return facility; }
    public void setFacility(Facility facility) { this.facility = facility; }
    public MachineStatus getStatus() { return status; }
    public void setStatus(MachineStatus status) { this.status = status; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public Integer getWaterLevel() { return waterLevel; }
    public void setWaterLevel(Integer waterLevel) { this.waterLevel = waterLevel; }
    public Integer getMilkLevel() { return milkLevel; }
    public void setMilkLevel(Integer milkLevel) { this.milkLevel = milkLevel; }
    public Integer getBeansLevel() { return beansLevel; }
    public void setBeansLevel(Integer beansLevel) { this.beansLevel = beansLevel; }
    public List<UsageHistory> getUsageHistory() { return usageHistory; }
    public void setUsageHistory(List<UsageHistory> usageHistory) { this.usageHistory = usageHistory != null ? usageHistory : new ArrayList<>(); }
    public List<Alert> getAlerts() { return alerts; }
    public void setAlerts(List<Alert> alerts) { this.alerts = alerts != null ? alerts : new ArrayList<>(); }
}