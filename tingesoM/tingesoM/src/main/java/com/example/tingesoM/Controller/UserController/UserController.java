package com.example.tingesoM.Controller.UserController;

import com.example.tingesoM.Dtos.CreateUserDto;
import com.example.tingesoM.Entities.User;
import com.example.tingesoM.Service.ServiceImpl.Users.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins="http://Localhost:8070")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/createUser")
    public ResponseEntity<CreateUserDto> createUser(@RequestBody CreateUserDto user) {
        userService.save(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getByEmail")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<CreateUserDto> updateUser(@RequestBody CreateUserDto user) {
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }
}
