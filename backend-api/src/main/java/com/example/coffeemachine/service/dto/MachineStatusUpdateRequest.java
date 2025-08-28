package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.MachineStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MachineStatusUpdateRequest {
    @NotNull
    private Long machineId;

    private MachineStatus status;

    private BigDecimal temperature;

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
}
