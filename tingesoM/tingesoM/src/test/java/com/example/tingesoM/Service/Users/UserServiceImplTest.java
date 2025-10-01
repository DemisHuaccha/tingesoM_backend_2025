package com.example.tingesoM.Service.Users;

import com.example.tingesoM.Dtos.CreateUserDto;
import com.example.tingesoM.Entities.User;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import com.example.tingesoM.Service.ServiceImpl.Users.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepositorie userRepositorie;

    private static final String TEST_EMAIL = "testuser@example.com";


    @Test
    @Order(1)
    void save() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(TEST_EMAIL);
        dto.setRole("ADMIN");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("+56911111111");

        userService.save(dto);

        User saved = userService.findByEmail(TEST_EMAIL);
        assertNotNull(saved);
        assertEquals("ADMIN", saved.getRole());
        assertFalse(saved.getDeleteStatus());
    }

    @Test
    @Order(2)
    void findById() {
        User user = userService.findByEmail(TEST_EMAIL);
        assertNotNull(user);

        User found = userService.findById(user.getUser_id());
        assertNotNull(found);
        assertEquals(TEST_EMAIL, found.getEmail());
    }

    @Test
    @Order(3)
    void findByEmail() {
        User user = userService.findByEmail(TEST_EMAIL);
        assertNotNull(user);
        assertEquals("Test", user.getUser_firstname());
    }

    @Test
    @Order(4)
    void findAll() {
        List<User> users = userService.findAll();
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(5)
    void updateUser() {
        CreateUserDto updatedDto = new CreateUserDto();
        updatedDto.setEmail(TEST_EMAIL);
        updatedDto.setRole("USER");
        updatedDto.setFirstName("Updated");
        updatedDto.setLastName("Name");
        updatedDto.setPhone("+56922222222");

        userService.updateUser(updatedDto);

        User updated = userService.findByEmail(TEST_EMAIL);
        assertEquals("Updated", updated.getUser_firstname());
        assertEquals("USER", updated.getRole());
        assertEquals("+56922222222", updated.getPhone());
    }
}
