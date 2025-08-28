package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.BrewType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MachineUsageEventRequest {
    @NotNull
    private Long machineId;

    @NotNull
    private BrewType brewType;

    @Min(0)
    private Integer volumeMl;

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public BrewType getBrewType() {
        return brewType;
    }

    public void setBrewType(BrewType brewType) {
        this.brewType = brewType;
    }

    public Integer getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(Integer volumeMl) {
        this.volumeMl = volumeMl;
    }
}
