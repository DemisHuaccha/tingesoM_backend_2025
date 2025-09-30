package com.example.tingesoM.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("moveDate")
    private LocalDate moveDate;

    @JsonProperty("typeMove")
    private String typeMove;

    @Column(name = "description")
    private String description;

    //Valido en movimientos que tengan que ver con cobro
    @Column(name = "amount")
    private Integer amount;

    //Valido en movimientos que involucren cantidad
    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_Tool", nullable = true)
    private Tool tool;

    @ManyToOne
    @JoinColumn(name = "id_Loan", nullable = true)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = true)
    private Client client;


}