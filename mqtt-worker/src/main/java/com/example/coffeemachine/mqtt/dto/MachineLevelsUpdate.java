package com.example.coffeemachine.mqtt.dto;

public class MachineLevelsUpdate {
    private Long machineId;
    private Integer waterLevel;
    private Integer milkLevel;
    private Integer beansLevel;

    public MachineLevelsUpdate() {
    }

    public MachineLevelsUpdate(Long machineId, Integer waterLevel, Integer milkLevel, Integer beansLevel) {
        this.machineId = machineId;
        this.waterLevel = waterLevel;
        this.milkLevel = milkLevel;
        this.beansLevel = beansLevel;
    }

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