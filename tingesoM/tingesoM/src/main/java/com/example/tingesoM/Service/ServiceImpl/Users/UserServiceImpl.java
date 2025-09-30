package com.example.tingesoM.Service.ServiceImpl.Users;

import com.example.tingesoM.Dtos.CreateUserDto;
import com.example.tingesoM.Entities.User;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import com.example.tingesoM.Service.Interface.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositorie userRepositorie;

    @Override
    public void save(CreateUserDto userInf){
        Optional<User> userP=userRepositorie.findByEmail((userInf.getEmail()));
        if(userP.isPresent()){
            return;
        }
        User user=new User();
        user.setDeleteStatus(false);
        user.setEmail(userInf.getEmail());
        user.setRole(userInf.getRole());
        userRepositorie.save(user);
    }

    @Override
    public User findById(Long id){
        return userRepositorie.findById(id).orElse(null);
    }

    public User findByEmail(String email){
        return userRepositorie.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepositorie.findAll();
    }

    //Update

    public void updateUser(CreateUserDto user){
        Optional<User> userP=userRepositorie.findByEmail(user.getEmail());
        if(userP.isPresent()){
            User oldUser=userP.get();
            oldUser.setEmail(user.getEmail());
            oldUser.setRole(user.getRole());
            oldUser.setUser_firstname(user.getFirstName());
            oldUser.setUser_lastname(user.getLastName());
            oldUser.setPhone(user.getPhone());
            userRepositorie.save(oldUser);
        }else {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " not found");
        }
    }
}
