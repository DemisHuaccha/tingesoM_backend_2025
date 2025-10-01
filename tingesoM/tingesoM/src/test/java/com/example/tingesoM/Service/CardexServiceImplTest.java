package com.example.tingesoM.Service;

import com.example.tingesoM.Dtos.*;
import com.example.tingesoM.Entities.*;
import com.example.tingesoM.Repositorie.*;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import com.example.tingesoM.Service.ServiceImpl.CardexServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class CardexServiceImplTest {

    @Autowired
    private CardexServiceImpl cardexService;

    @Autowired
    private CardexRepositorie cardexRepositorie;

    @Autowired
    private UserRepositorie userRepositorie;

    @Autowired
    private ToolRepositorie toolRepositorie;

    @Autowired
    private LoanRepositorie loanRepositorie;

    @Autowired
    private ClientRepositorie clientRepositorie;

    private static String userEmail = "admin@example.com";
    private static Long toolId;
    private static Long loanId;
    private static String clientRut = "12.345.678-9";

    @BeforeAll
    static void setup(@Autowired UserRepositorie userRepo,
                      @Autowired ToolRepositorie toolRepo,
                      @Autowired ClientRepositorie clientRepo,
                      @Autowired LoanRepositorie loanRepo) {

        User user = new User();
        user.setEmail(userEmail);
        user.setUser_firstname("Admin");
        user.setRole("ADMIN");
        userRepo.save(user);

        Tool tool = new Tool();
        tool.setName("Taladro");
        tool.setCategory("Electricas");
        tool.setLoanFee(10000);
        tool.setUnderRepair(false);
        Tool savedTool = toolRepo.save(tool);
        toolId = savedTool.getIdTool();

        Client client = new Client();
        client.setFirstName("Ana");
        client.setLastName("Torres");
        client.setRut(clientRut);
        client.setEmail("ana@example.com");
        client.setPhone("+56912345678");
        client.setStatus(true);
        clientRepo.save(client);

        Loan loan = new Loan();
        loan.setClient(client);
        loan.setTool(tool);
        loan.setDeliveryDate(LocalDate.now().minusDays(5));
        loan.setReturnDate(LocalDate.now().plusDays(5));
        loanRepo.save(loan);
        loanId = loan.getLoanId();
    }

    @Test
    @Order(1)
    void saveCardexLoan() {
        Loan loan = loanRepositorie.findById(loanId).orElseThrow();
        cardexService.saveCardexLoan(toolId, userEmail, loan);

        List<Cardex> result = cardexRepositorie.findAll();
        assertTrue(result.stream().anyMatch(c -> "Create Loan".equals(c.getTypeMove())));
    }

    @Test
    @Order(2)
    void saveCardexTool() {
        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(userEmail);
        dto.setQuantity(3);

        Tool tool = toolRepositorie.findById(toolId).orElseThrow();
        cardexService.saveCardexTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Create Tool".equals(c.getTypeMove())));
    }

    @Test
    @Order(3)
    void saveCardexClient() {
        CreateClientDto dto = new CreateClientDto();
        dto.setEmail(userEmail);
        dto.setRut(clientRut);

        Client client = clientRepositorie.findByRut(clientRut).orElseThrow();
        cardexService.saveCardexClient(dto, client);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Create Client".equals(c.getTypeMove())));
    }

    @Test
    @Order(4)
    void saveCardexUpdateTool() {
        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(userEmail);

        Tool tool = toolRepositorie.findById(toolId).orElseThrow();
        cardexService.saveCardexUpdateTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Update Tool".equals(c.getTypeMove())));
    }

    @Test
    @Order(5)
    void saveCardexUpdateStatusTool() {
        ToolStatusDto dto = new ToolStatusDto();
        dto.setEmail(userEmail);
        dto.setIdTool(toolId);

        Tool tool = toolRepositorie.findById(toolId).orElseThrow();
        cardexService.saveCardexUpdateStatusTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> c.getTypeMove().startsWith("Tool update status")));
    }

    @Test
    @Order(6)
    void saveCardexRepairTool() {
        ToolStatusDto dto = new ToolStatusDto();
        dto.setEmail(userEmail);
        dto.setIdTool(toolId);

        Tool tool = toolRepositorie.findById(toolId).orElseThrow();
        cardexService.saveCardexRepairTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> c.getTypeMove().startsWith("Tool Repair status")));
    }

    @Test
    @Order(7)
    void saveCardexDeleteTool() {
        ToolStatusDto dto = new ToolStatusDto();
        dto.setEmail(userEmail);
        dto.setIdTool(toolId);

        Tool tool = toolRepositorie.findById(toolId).orElseThrow();
        cardexService.saveCardexDeleteTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Tool Delete".equals(c.getTypeMove())));
    }

    @Test
    @Order(8)
    void saveCardexReturnLoan() {
        ReturnLoanDto dto = new ReturnLoanDto();
        dto.setEmail(userEmail);
        dto.setLoanId(loanId);

        cardexService.saveCardexReturnLoan(dto);
        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Loan Finished".equals(c.getTypeMove())));
    }

    @Test
    @Order(9)
    void findAllDto() {
        List<CardexDto> dtos = cardexService.findAllDto();

        assertFalse(dtos.isEmpty());
    }

    @Test
    @Order(10)
    void findForRangeDate() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().plusDays(10);

        List<CardexDto> dtos = cardexService.findForRangeDate(start, end);
        assertFalse(dtos.isEmpty());
    }

    @Test
    @Order(11)
    void findCardexTool() {
        List<CardexDto> dtos = cardexService.findCardexTool(toolId);
        System.out.println(toolId);
        assertFalse(dtos.isEmpty());
        assertEquals(toolId, dtos.get(0).getToolId());
    }
}
