package com.example.coffeemachine.service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateMachineStatusRequest {
    private String status;
    private BigDecimal temperature;
}
