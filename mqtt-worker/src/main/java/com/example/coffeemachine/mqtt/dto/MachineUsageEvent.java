package com.example.coffeemachine.mqtt.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MachineUsageEvent {
    private Long machineId;
    private String brewType;
    private Integer volumeMl;
    private BigDecimal tempAtBrew;
}