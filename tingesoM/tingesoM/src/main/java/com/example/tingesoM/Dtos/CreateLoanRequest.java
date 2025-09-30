package com.example.tingesoM.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateLoanRequest {
    private LocalDate deliveryDate;
    private LocalDate returnDate;
    private String clientRut;
    private Long toolId;

    /*---------------*/

    private String email;
}
