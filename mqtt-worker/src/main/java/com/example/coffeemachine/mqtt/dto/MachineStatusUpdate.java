package com.example.coffeemachine.mqtt.dto;

import java.math.BigDecimal;

public class MachineStatusUpdate {
    private Long machineId;
    private String status;
    private BigDecimal temperature;

    public MachineStatusUpdate() {
    }

    public MachineStatusUpdate(Long machineId, String status, BigDecimal temperature) {
        this.machineId = machineId;
        this.status = status;
        this.temperature = temperature;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
}