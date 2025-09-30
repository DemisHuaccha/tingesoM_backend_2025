package com.example.tingesoM.Dtos;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CardexDto{
    public Long id;
    public LocalDate moveDate;
    public String typeMove;
    public String description;
    public Integer amount;
    public Integer quantity;
    public String userEmail;
    public Long toolId;
    public Long loanId;
    public String clientRut;
    public CardexDto(Long id, LocalDate moveDate, String typeMove, String description, Integer amount,
                     Integer quantity, String userEmail, Long toolId, Long loanId, String clientRut) {
        this.id = id;
        this.moveDate = moveDate;
        this.typeMove = typeMove;
        this.description = description;
        this.amount = amount;
        this.quantity = quantity;
        this.userEmail = userEmail;
        this.toolId = toolId;
        this.loanId = loanId;
        this.clientRut = clientRut;
    }
}
