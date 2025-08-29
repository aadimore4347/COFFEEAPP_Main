package com.example.coffeemachine.service.dto;

import com.example.coffeemachine.domain.BrewType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecordUsageRequest {
    private BrewType brewType;
    private Integer volumeMl;
    private BigDecimal tempAtBrew;
}
