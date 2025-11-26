package com.example.tingesoM.Dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateLoanRequestA {
    private LocalDate deliveryDate;
    private LocalDate returnDate;
    private String clientRut;
    private String name;
    private String category;
    private InitialCondition initialCondition;
    private Integer loanFee;
    private Integer penaltyForDelay;
    private Integer replacementValue;
    private Integer damageValue;
    private List<Long> ToolId;

    /*---------------*/

    private String email;
}
