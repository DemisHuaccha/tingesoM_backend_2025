package com.example.tingesoM.Dtos;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CreateToolDto {
    private String name;
    private String description;
    private String category;
    private InitialCondition initialCondition;
    private Integer loanFee;
    private Integer penaltyForDelay;
    private Integer replacementValue;
    private Boolean status;
    private Boolean underRepair;
    private Boolean deleteStatus;

    /*--------------*/
    private Integer quantity;
    private String email;
    private Long idTool;
}
