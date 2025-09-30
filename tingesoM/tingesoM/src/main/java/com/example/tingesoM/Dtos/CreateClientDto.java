package com.example.tingesoM.Dtos;

import lombok.Data;

@Data
public class CreateClientDto {

    private String firstName;
    private String lastName;
    private String rut;
    private Boolean status;
    private String emailC;
    private String phone;

    /*----------------------*/

    private String email;


}
