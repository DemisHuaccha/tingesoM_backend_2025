package com.example.tingesoM.Entities;

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
@Table(name = "loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(unique = true, nullable = false)
    private Long loanId;

    @Column(name = "deliveryDate")
    private LocalDate deliveryDate;

    @Column(name = "returnDate")
    private LocalDate returnDate;

    @ManyToOne
    @JoinColumn(name = "id_Customer")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_Tool")
    private Tool tool;

    //True es que esta activa
    //False es que ya fue completada(Es decir no se tiene en cuenta)
    @Column(name = "loan_status")
    private Boolean loanStatus;

    //Booleano que indica si hay atraso
    @Column(name= "penalty")
    private Boolean penalty;

    @Column(name = "penalty_Total")
    private Integer penaltyTotal;
}
