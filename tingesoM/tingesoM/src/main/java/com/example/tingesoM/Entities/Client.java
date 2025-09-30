package com.example.tingesoM.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomer;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;


    @Column(name = "rut", unique = true)
    private String rut;

    //True= Permitid
    //False= Restrict
    @Column(name = "status")
    private Boolean status;

}
