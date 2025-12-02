package com.example.tingesoM.Service;

import com.example.tingesoM.Dtos.*;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Service.ServiceImpl.ToolServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.example.tingesoM.Entities.User;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import org.junit.jupiter.api.Test;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ToolServiceImplTest {

    @Autowired
    private ToolServiceImpl toolService;

    @Autowired
    private ToolRepositorie toolRepositorie;

    @Autowired
    private UserRepositorie userRepositorie;

    // --- Helpers para crear datos dentro de los tests ---

    private User createAndSaveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setRole("ADMIN");
        user.setUser_firstname("Admin");
        user.setUser_lastname("User");
        user.setPhone("+56999999999");
        user.setDeleteStatus(false);
        return userRepositorie.save(user);
    }

    private Tool createAndSaveTool(String name) {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setCategory("Manuales");
        tool.setInitialCondition(InitialCondition.New);
        tool.setLoanFee(1000);
        tool.setPenaltyForDelay(500);
        tool.setReplacementValue(5000);
        tool.setDescription("Descripción de prueba");
        tool.setStatus(true);
        tool.setDeleteStatus(false);
        tool.setUnderRepair(false);
        tool.setDamageValue(200);
        return toolRepositorie.save(tool);
    }

    // --- Tests ---

    @Test
    void save() {
        String email = "save@test.com";
        createAndSaveUser(email);

        CreateToolDto dto = new CreateToolDto();
        dto.setName("Martillo");
        dto.setCategory("Manuales");
        dto.setInitialCondition(InitialCondition.New);
        dto.setLoanFee(1000);
        dto.setPenaltyForDelay(500);
        dto.setReplacementValue(5000);
        dto.setDamageValue(200);
        dto.setDescription("Herramienta de golpe");
        dto.setQuantity(1);
        dto.setStatus(Boolean.TRUE);
        dto.setUnderRepair(Boolean.FALSE);
        dto.setDeleteStatus(Boolean.FALSE);
        dto.setEmail(email); // IMPORTANTE: El servicio requiere un usuario existente

        Tool saved = toolService.save(dto);

        assertNotNull(saved);
        assertNotNull(saved.getIdTool());
        assertEquals("Martillo", saved.getName());
    }

    @Test
    void save_MultipleQuantity() {
        String email = "multi@test.com";
        createAndSaveUser(email);

        CreateToolDto dto = new CreateToolDto();
        dto.setName("Clavo");
        dto.setCategory("Insumos");
        dto.setEmail(email);
        dto.setQuantity(3); // Solicitamos crear 3

        // Rellenar datos obligatorios
        dto.setInitialCondition(InitialCondition.New);
        dto.setLoanFee(10);
        dto.setPenaltyForDelay(5);
        dto.setReplacementValue(50);
        dto.setDamageValue(10);

        toolService.save(dto);

        // Verificamos que se hayan creado 3 herramientas
        long count = toolRepositorie.findAll().stream()
                .filter(t -> t.getName().equals("Clavo"))
                .count();
        assertEquals(3, count);
    }

    @Test
    void findById() {
        Tool original = createAndSaveTool("Sierra");

        Tool found = toolService.findById(original.getIdTool());

        assertNotNull(found);
        assertEquals(original.getIdTool(), found.getIdTool());
    }

    @Test
    void findAll() {
        createAndSaveTool("Tool A");
        createAndSaveTool("Tool B");

        List<Tool> tools = toolService.findAll();
        assertFalse(tools.isEmpty());
        assertTrue(tools.size() >= 2);
    }

    @Test
    void findAllAvalible() {
        Tool t1 = createAndSaveTool("Disponible");

        Tool t2 = createAndSaveTool("Ocupada");
        t2.setStatus(false);
        toolRepositorie.save(t2);

        List<Tool> availableTools = toolService.findAllAvalible();

        assertTrue(availableTools.stream().anyMatch(t -> t.getName().equals("Disponible")));
        assertFalse(availableTools.stream().anyMatch(t -> t.getName().equals("Ocupada")));
    }

    @Test
    void findAllNotDelete() {
        Tool t1 = createAndSaveTool("Activa");

        Tool t2 = createAndSaveTool("Borrada");
        t2.setDeleteStatus(true);
        toolRepositorie.save(t2);

        List<Tool> activeTools = toolService.findAllNotDelete();

        assertTrue(activeTools.stream().anyMatch(t -> t.getName().equals("Activa")));
        assertFalse(activeTools.stream().anyMatch(t -> t.getName().equals("Borrada")));
    }

    @Test
    void getConditions() {
        List<String> conditions = toolService.getConditions();
        assertFalse(conditions.isEmpty());
        assertTrue(conditions.contains("New"));
        assertTrue(conditions.contains("Good"));
    }

    @Test
    void updateTool() {
        String email = "update@test.com";
        createAndSaveUser(email);
        Tool tool = createAndSaveTool("Viejo Nombre");

        CreateToolDto dto = new CreateToolDto();
        dto.setIdTool(tool.getIdTool());
        dto.setName("Nuevo Nombre");
        dto.setCategory("Manuales");
        dto.setInitialCondition(InitialCondition.Good);
        dto.setLoanFee(1200);
        dto.setPenaltyForDelay(600);
        dto.setReplacementValue(6000);
        dto.setEmail(email); // Necesario para el Cardex

        Tool updated = toolService.updateTool(dto);

        assertEquals("Nuevo Nombre", updated.getName());
        assertEquals(1200, updated.getLoanFee());
    }

    @Test
    void updateStatusTool() {
        String email = "status@test.com";
        createAndSaveUser(email);
        Tool tool = createAndSaveTool("Test Status");
        assertTrue(tool.getStatus()); // Empieza en true

        ToolStatusDto dto = new ToolStatusDto();
        dto.setIdTool(tool.getIdTool());
        dto.setStatus(true); // El servicio invierte el valor si coincide.
        dto.setEmail(email);

        toolService.updateStatusTool(dto);

        Tool updated = toolRepositorie.findById(tool.getIdTool()).orElseThrow();
        assertFalse(updated.getStatus());
    }

    @Test
    void underRepairTool() {
        String email = "repair@test.com";
        createAndSaveUser(email);
        Tool tool = createAndSaveTool("Test Repair");
        assertFalse(tool.getUnderRepair()); // Empieza en false

        ToolStatusDto dto = new ToolStatusDto();
        dto.setIdTool(tool.getIdTool());
        dto.setUnderRepair(false); // El servicio invierte si es false -> true
        dto.setEmail(email);

        toolService.underRepairTool(dto);

        Tool updated = toolRepositorie.findById(tool.getIdTool()).orElseThrow();
        assertTrue(updated.getUnderRepair());
    }

    @Test
    void deleteTool() {
        String email = "delete@test.com";
        createAndSaveUser(email);
        Tool tool = createAndSaveTool("Test Delete");
        assertFalse(tool.getDeleteStatus());

        ToolStatusDto dto = new ToolStatusDto();
        dto.setIdTool(tool.getIdTool());
        dto.setDeleteStatus(false); // El servicio invierte false -> true
        dto.setEmail(email);

        toolService.deleteTool(dto);

        Tool updated = toolRepositorie.findById(tool.getIdTool()).orElseThrow();
        assertTrue(updated.getDeleteStatus());
    }

    @Test
    void filterTools() {
        createAndSaveTool("FiltroTest");

        ToolRankingDto dto = new ToolRankingDto("FiltroTest", "Manuales", 1000, null);

        List<Tool> filtered = toolService.filterTools(dto);

        assertFalse(filtered.isEmpty());
        assertEquals("FiltroTest", filtered.get(0).getName());
    }

    @Test
    void groupTools() {
        // Test simple para verificar que no falle la llamada
        createAndSaveTool("GroupA");
        createAndSaveTool("GroupA"); // Duplicado para agrupar

        List<GroupToolsDto> groups = toolService.groupTools();
        assertNotNull(groups);
        // La validación profunda depende de la query en el repositorio
    }
}