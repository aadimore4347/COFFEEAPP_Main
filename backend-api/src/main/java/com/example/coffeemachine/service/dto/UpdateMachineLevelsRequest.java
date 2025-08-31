package com.example.coffeemachine.service.dto;

import lombok.Data;

@Data
public class UpdateMachineLevelsRequest {
    private Integer waterLevel;
    private Integer milkLevel;
    private Integer beansLevel;
}
