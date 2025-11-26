package com.example.tingesoM.Dtos;

import lombok.Data;

@Data
public class GroupToolsDto {
    private String name;
    private String category;
    private InitialCondition initialCondition;
    private Integer loanFee;
    private Integer penaltyForDelay;
    private Integer replacementValue;
    private Integer damageValue;
    private Long stock;

    public GroupToolsDto(String name, String category, InitialCondition initialCondition, Integer loanFee,
                         Integer penaltyForDelay, Integer replacementValue, Integer damageValue, Long stock) {
        this.name = name;
        this.category = category;
        this.initialCondition = initialCondition;
        this.loanFee = loanFee;
        this.penaltyForDelay = penaltyForDelay;
        this.replacementValue = replacementValue;
        this.damageValue = damageValue;
        this.stock = stock;
    }

}
