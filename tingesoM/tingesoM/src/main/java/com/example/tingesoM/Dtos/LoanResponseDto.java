package com.example.tingesoM.Dtos;

import lombok.Data;

import java.time.LocalDate;


@Data
public class LoanResponseDto {
    private Long loanId;
    private LocalDate deliveryDate;
    private LocalDate returnDate;
    private Boolean loanStatus;
    private Boolean penalty;
    private Integer penaltyTotal;
    private String clientRut;
    private Long toolId;

    public LoanResponseDto(Long loanId, LocalDate deliveryDate, LocalDate returnDate, Boolean loanStatus,
                           Boolean penalty, Integer penaltyTotal, String clientRut, Long toolId) {
        this.loanId = loanId;
        this.deliveryDate = deliveryDate;
        this.returnDate = returnDate;
        this.loanStatus = loanStatus;
        this.penalty = penalty;
        this.penaltyTotal = penaltyTotal;
        this.clientRut = clientRut;
        this.toolId = toolId;
    }
}