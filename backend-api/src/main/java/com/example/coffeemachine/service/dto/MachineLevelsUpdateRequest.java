package com.example.coffeemachine.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MachineLevelsUpdateRequest {
    @NotNull
    private Long machineId;

    @Min(0) @Max(100)
    private Integer waterLevel;

    @Min(0) @Max(100)
    private Integer milkLevel;

    @Min(0) @Max(100)
    private Integer beansLevel;

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }

    public Integer getMilkLevel() {
        return milkLevel;
    }

    public void setMilkLevel(Integer milkLevel) {
        this.milkLevel = milkLevel;
    }

    public Integer getBeansLevel() {
        return beansLevel;
    }

    public void setBeansLevel(Integer beansLevel) {
        this.beansLevel = beansLevel;
    }
}
