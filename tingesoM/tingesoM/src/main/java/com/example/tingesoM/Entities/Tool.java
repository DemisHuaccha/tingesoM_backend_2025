package com.example.tingesoM.Entities;

import com.example.tingesoM.Dtos.InitialCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name= "tool")
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTool;

    @Column(name = "toolName")
    private String name;

    @Column(name = "toolDescription")
    private String description;

    @Column(name = "toolCategory")
    private String category;

    @Column(name= "tool_initia_condition")
    private InitialCondition initialCondition;
    //Precio de prestamo diario
    @Column(name = "loan_Fee")
    private Integer loanFee;
    //Multa diaria por atraso
    @Column(name = "penalty_for_delay")
    private Integer penaltyForDelay;
    //Precio de reemplazo
    @Column(name = "replacementValue")
    private Integer replacementValue;

    //Herramienta libre: True (Libre) / False (En prestamo)
    @Column(name = "status")
    private Boolean status;

    @Column(name = "under_repair")
    private Boolean underRepair;

    @Column(name = "deleteStatus")
    private Boolean deleteStatus;


}
