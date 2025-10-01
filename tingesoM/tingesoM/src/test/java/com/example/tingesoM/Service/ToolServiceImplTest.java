package com.example.tingesoM.Service;

import com.example.tingesoM.Dtos.*;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Service.ServiceImpl.ToolServiceImpl;
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
class ToolServiceImplTest {

    @Autowired
    private ToolServiceImpl toolService;

    @Autowired
    private ToolRepositorie toolRepositorie;

    private static Long toolId;
    @Autowired
    private UserServiceImpl userServiceImpl;


    @BeforeEach
    void setContext() {
        String testEmail = "admin@example.com";

        CreateUserDto userDto = new CreateUserDto();
        userDto.setEmail(testEmail);
        userDto.setRole("ADMIN");
        userDto.setFirstName("Admin");
        userDto.setLastName("User");
        userDto.setPhone("+56999999999");
        userServiceImpl.save(userDto);
    }


        @Test
    @Order(1)
    void save() {
        CreateToolDto dto = new CreateToolDto();
        dto.setName("Martillo");
        dto.setCategory("Manuales");
        dto.setInitialCondition(InitialCondition.New);
        dto.setLoanFee(1000);
        dto.setPenaltyForDelay(500);
        dto.setReplacementValue(5000);
        dto.setDescription("Herramienta de golpe");
        dto.setQuantity(3);
        dto.setStatus(Boolean.TRUE);
        dto.setUnderRepair(Boolean.FALSE);
        dto.setDeleteStatus(Boolean.FALSE);

        Tool saved = toolService.save(dto);
        toolId = saved.getIdTool();

        assertNotNull(saved);
        assertEquals("Martillo", saved.getName());
    }

    @Test
    @Order(2)
    void findById() {
        Tool tool = toolService.findById(toolId);
        assertNotNull(tool);
        assertEquals("Martillo", tool.getName());
    }

    @Test
    @Order(3)
    void findAll() {
        List<Tool> tools = toolService.findAll();
        assertFalse(tools.isEmpty());
    }

    @Test
    @Order(4)
    void findAllAvalible() {
        List<Tool> tools = toolService.findAllAvalible();
        assertTrue(tools.stream().allMatch(Tool::getStatus));
    }

    @Test
    @Order(5)
    void findAllNotDelete() {
        List<Tool> tools = toolService.findAllNotDelete();
        assertTrue(tools.stream().allMatch(t -> !t.getDeleteStatus()));
    }

    @Test
    @Order(6)
    void getConditions() {
        List<String> conditions = toolService.getConditions();
        assertTrue(conditions.contains("New"));
    }

    @Test
    @Order(7)
    void updateTool() {
        CreateToolDto dto = new CreateToolDto();
        dto.setIdTool(toolId);
        dto.setName("Martillo Pro");
        dto.setCategory("Manuales");
        dto.setInitialCondition(InitialCondition.Good);
        dto.setLoanFee(1200);
        dto.setPenaltyForDelay(600);
        dto.setReplacementValue(6000);
        dto.setEmail("admin@example.com");

        Tool updated = toolService.updateTool(dto);
        assertEquals("Martillo Pro", updated.getName());
        assertEquals(InitialCondition.Good, updated.getInitialCondition());
    }

    @Test
    @Order(8)
    void updateStatusTool() {
        // Crear herramienta independiente
        Tool tool = new Tool();
        tool.setName("Destornillador");
        tool.setCategory("Manuales");
        tool.setInitialCondition(InitialCondition.New);
        tool.setLoanFee(800);
        tool.setPenaltyForDelay(400);
        tool.setReplacementValue(3000);
        tool.setDescription("Herramienta de precisión");
        tool.setStatus(true);
        tool.setDeleteStatus(false);
        tool.setUnderRepair(false);
        toolRepositorie.save(tool);


        ToolStatusDto dto = new ToolStatusDto();
        dto.setIdTool(tool.getIdTool());
        dto.setStatus(true); // cambiar a false
        dto.setEmail("admin@example.com");

        toolService.updateStatusTool(dto);
        Tool updated = toolService.findById(tool.getIdTool());
        assertFalse(updated.getStatus());
    }

    @Test
    @Order(9)
    void underRepairTool() {
        Tool tool = new Tool();
        tool.setName("Sierra");
        tool.setCategory("Eléctricas");
        tool.setInitialCondition(InitialCondition.Good);
        tool.setLoanFee(1500);
        tool.setPenaltyForDelay(700);
        tool.setReplacementValue(7000);
        tool.setDescription("Herramienta de corte");
        tool.setStatus(true);
        tool.setDeleteStatus(false);
        tool.setUnderRepair(false);
        toolRepositorie.save(tool);

        ToolStatusDto dto = new ToolStatusDto();
        dto.setIdTool(tool.getIdTool());
        dto.setUnderRepair(false); // cambiar a true
        dto.setEmail("admin@example.com");

        toolService.underRepairTool(dto);
        Tool updated = toolService.findById(tool.getIdTool());
        assertTrue(updated.getUnderRepair());
    }

    @Test
    @Order(10)
    void deleteTool() {
        Tool tool = new Tool();
        tool.setName("Taladro");
        tool.setCategory("Eléctricas");
        tool.setInitialCondition(InitialCondition.Minor_Damage);
        tool.setLoanFee(2000);
        tool.setPenaltyForDelay(1000);
        tool.setReplacementValue(10000);
        tool.setDescription("Herramienta de perforación");
        tool.setStatus(true);
        tool.setDeleteStatus(false);
        tool.setUnderRepair(false);
        toolRepositorie.save(tool);

        ToolStatusDto dto = new ToolStatusDto();
        dto.setIdTool(tool.getIdTool());
        dto.setDeleteStatus(false); // cambiar a true
        dto.setEmail("admin@example.com");

        toolService.deleteTool(dto);
        Tool updated = toolService.findById(tool.getIdTool());
        assertTrue(updated.getDeleteStatus());
    }

    @Test
    @Order(11)
    void findAllToolLoanRanking() {
        List<ToolRankingDto> ranking = toolService.findAllToolLoanRanking();
        assertNotNull(ranking);
    }

    @Test
    @Order(12)
    void filterTools() {
        ToolRankingDto dto = new ToolRankingDto("Martillo Pro","Manuales",1200,null );
        List<Tool> filtered = toolService.filterTools(dto);
        assertTrue(filtered.stream().anyMatch(t -> t.getName().equals("Martillo Pro")));
    }
}
