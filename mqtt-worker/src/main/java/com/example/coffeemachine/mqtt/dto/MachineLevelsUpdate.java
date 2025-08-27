package com.example.coffeemachine.mqtt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MachineLevelsUpdate {
    private Long machineId;
    private Integer waterLevel;
    private Integer milkLevel;
    private Integer beansLevel;
}