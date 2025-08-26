package com.example.coffeemachine.mqtt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MachineAlertEvent {
    private Long machineId;
    private String alertType;
    private String severity;
    private String message;
    private Integer threshold;
}