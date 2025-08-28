package com.example.coffeemachine.mqtt.dto;

public class MachineAlertEvent {
    private Long machineId;
    private String alertType;
    private String severity;
    private String message;
    private Integer threshold;

    public MachineAlertEvent() {
    }

    public MachineAlertEvent(Long machineId, String alertType, String severity, String message, Integer threshold) {
        this.machineId = machineId;
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.threshold = threshold;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}