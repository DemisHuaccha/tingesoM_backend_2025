package com.example.tingesoM.Service.Users;

import com.example.tingesoM.Dtos.CreateUserDto;
import com.example.tingesoM.Entities.User;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import com.example.tingesoM.Service.ServiceImpl.Users.UserServiceImpl;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepositorie userRepositorie;

    // --- Helper para crear datos dentro de los tests ---
    private User createAndSaveUser(String email, String role, String name) {
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setUser_firstname(name);
        user.setUser_lastname("TestLast");
        user.setPhone("+56911111111");
        user.setDeleteStatus(false);
        return userRepositorie.save(user);
    }

    @Test
    void save() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("newuser@example.com");
        dto.setRole("ADMIN");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setPhone("+56912345678");

        userService.save(dto);

        User saved = userRepositorie.findByEmail("newuser@example.com").orElse(null);
        assertNotNull(saved);
        assertEquals("ADMIN", saved.getRole());
        assertFalse(saved.getDeleteStatus());
    }

    @Test
    void save_ExistingUser_ShouldDoNothing() {
        // 1. Crear usuario previo
        createAndSaveUser("exist@example.com", "USER", "OldName");

        // 2. Intentar guardar el mismo email
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("exist@example.com");
        dto.setFirstName("NewName"); // Intentamos cambiar nombre

        userService.save(dto);

        // 3. Verificar que NO cambió (tu lógica hace return si existe)
        User current = userRepositorie.findByEmail("exist@example.com").orElseThrow();
        assertEquals("OldName", current.getUser_firstname());
    }

    @Test
    void findById() {
        User original = createAndSaveUser("idtest@example.com", "USER", "IdTest");

        User found = userService.findById(original.getUser_id());

        assertNotNull(found);
        assertEquals(original.getUser_id(), found.getUser_id());
    }

    @Test
    void findByEmail() {
        createAndSaveUser("emailtest@example.com", "USER", "EmailTest");

        User found = userService.findByEmail("emailtest@example.com");

        assertNotNull(found);
        assertEquals("EmailTest", found.getUser_firstname());
    }

    @Test
    void findAll() {
        createAndSaveUser("u1@ex.com", "USER", "U1");
        createAndSaveUser("u2@ex.com", "USER", "U2");

        List<User> users = userService.findAll();

        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 2);
    }

    @Test
    void updateUser() {
        // 1. Crear usuario original
        createAndSaveUser("update@example.com", "USER", "OriginalName");

        // 2. DTO con datos nuevos (mismo email)
        CreateUserDto updatedDto = new CreateUserDto();
        updatedDto.setEmail("update@example.com");
        updatedDto.setRole("ADMIN"); // Cambio de rol
        updatedDto.setFirstName("UpdatedName"); // Cambio de nombre
        updatedDto.setLastName("UpdatedLast");
        updatedDto.setPhone("+56999999999");

        userService.updateUser(updatedDto);

        // 3. Verificar cambios
        User updated = userRepositorie.findByEmail("update@example.com").orElseThrow();
        assertEquals("UpdatedName", updated.getUser_firstname());
        assertEquals("ADMIN", updated.getRole());
    }

    @Test
    void updateUser_NotFound_ShouldThrowException() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("nonexistent@example.com");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(dto);
        });
    }
}
