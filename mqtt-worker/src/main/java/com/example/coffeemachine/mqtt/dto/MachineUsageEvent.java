package com.example.coffeemachine.mqtt.dto;

import java.math.BigDecimal;

public class MachineUsageEvent {
    private Long machineId;
    private String brewType;
    private Integer volumeMl;
    private BigDecimal tempAtBrew;

    public MachineUsageEvent() {
    }

    public MachineUsageEvent(Long machineId, String brewType, Integer volumeMl, BigDecimal tempAtBrew) {
        this.machineId = machineId;
        this.brewType = brewType;
        this.volumeMl = volumeMl;
        this.tempAtBrew = tempAtBrew;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getBrewType() {
        return brewType;
    }

    public void setBrewType(String brewType) {
        this.brewType = brewType;
    }

    public Integer getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(Integer volumeMl) {
        this.volumeMl = volumeMl;
    }

    public BigDecimal getTempAtBrew() {
        return tempAtBrew;
    }

    public void setTempAtBrew(BigDecimal tempAtBrew) {
        this.tempAtBrew = tempAtBrew;
    }
}