package com.example.tingesoM.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(name = "user_firstname")
    private String user_firstname;

    @Column(name = "user_lastname")
    private String user_lastname;

    @Column(name= "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "deleteStatus")
    private Boolean deleteStatus;

    @Column(name = "role", nullable = false)
    private String role;

}
