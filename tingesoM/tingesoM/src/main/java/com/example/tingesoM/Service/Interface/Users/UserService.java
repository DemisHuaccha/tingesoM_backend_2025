package com.example.tingesoM.Service.Interface.Users;

import com.example.tingesoM.Dtos.CreateUserDto;
import com.example.tingesoM.Entities.User;

import java.util.List;

public interface UserService {
    void save(CreateUserDto user);
    User findById(Long id);
    List<User> findAll();
}
