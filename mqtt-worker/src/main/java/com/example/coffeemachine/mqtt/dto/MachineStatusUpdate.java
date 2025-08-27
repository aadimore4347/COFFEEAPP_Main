package com.example.coffeemachine.mqtt.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MachineStatusUpdate {
    private Long machineId;
    private String status;
    private BigDecimal temperature;
}