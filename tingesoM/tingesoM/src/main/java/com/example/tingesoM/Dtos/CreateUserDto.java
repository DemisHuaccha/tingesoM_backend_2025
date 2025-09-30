package com.example.tingesoM.Dtos;

import lombok.Data;


@Data
public class CreateUserDto {

    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String phone;
}
