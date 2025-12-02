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

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Limpia la DB después de cada test automáticamente
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

    // --- MÉTODOS HELPER PARA CREAR DATOS RÁPIDAMENTE ---

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setUser_firstname("TestUser");
        user.setRole("ADMIN");
        user.setDeleteStatus(false);
        return userRepositorie.save(user);
    }

    private Tool createTool(String name) {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setCategory("General");
        tool.setLoanFee(5000);
        tool.setUnderRepair(false);
        return toolRepositorie.save(tool);
    }

    private Client createClient(String rut) {
        Client client = new Client();
        client.setRut(rut);
        client.setFirstName("Cliente");
        client.setLastName("Test");
        client.setEmail("cli@test.com");
        client.setStatus(true);
        return clientRepositorie.save(client);
    }

    private Loan createLoan(Client client, Tool tool) {
        Loan loan = new Loan();
        loan.setClient(client);
        loan.setTool(tool);
        loan.setDeliveryDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(5));
        return loanRepositorie.save(loan);
    }

    // --- TESTS ---

    @Test
    void saveCardexLoan() {
        String email = "loan@test.com";
        createUser(email);
        Tool tool = createTool("Martillo");
        Client client = createClient("11.111.111-1");
        Loan loan = createLoan(client, tool);

        cardexService.saveCardexLoan(tool.getIdTool(), email, loan);

        List<Cardex> result = cardexRepositorie.findAll();
        assertTrue(result.stream().anyMatch(c -> "Create Loan".equals(c.getTypeMove())));
    }

    /*
    @Test
    void saveCardexLoanL() { // Test Faltante Agregado
        String email = "list@test.com";
        createUser(email);
        Client client = createClient("22.222.222-2");

        List<Loan> loans = new ArrayList<>();
        loans.add(createLoan(client, createTool("Tool A")));
        loans.add(createLoan(client, createTool("Tool B")));

        cardexService.saveCardexLoanL(loans, email);

        List<Cardex> result = cardexRepositorie.findAll();
        long count = result.stream().filter(c -> "Create Loan".equals(c.getTypeMove())).count();
        assertEquals(2, count);
    }
     */

    @Test
    void saveCardexTool() {
        String email = "tool@test.com";
        createUser(email);
        Tool tool = createTool("Sierra");

        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(email);
        dto.setQuantity(5);

        cardexService.saveCardexTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Create Tool".equals(c.getTypeMove())));
    }

    @Test
    void saveCardexClient() {
        String email = "client@test.com";
        createUser(email);
        Client client = createClient("33.333.333-3");

        CreateClientDto dto = new CreateClientDto();
        dto.setEmail(email);
        dto.setRut(client.getRut());

        cardexService.saveCardexClient(dto, client);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Create Client".equals(c.getTypeMove())));
    }

    @Test
    void saveCardexUpdateTool() {
        String email = "updtool@test.com";
        createUser(email);
        Tool tool = createTool("Lijadora");

        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(email);

        cardexService.saveCardexUpdateTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Update Tool".equals(c.getTypeMove())));
    }

    @Test
    void saveCardexUpdateStatusTool() {
        String email = "status@test.com";
        createUser(email);
        Tool tool = createTool("Llave");

        ToolStatusDto dto = new ToolStatusDto();
        dto.setEmail(email);
        dto.setIdTool(tool.getIdTool());

        cardexService.saveCardexUpdateStatusTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream()
                .anyMatch(c -> c.getTypeMove().startsWith("Tool update status")));
    }

    @Test
    void saveCardexRepairTool() {
        String email = "repair@test.com";
        createUser(email);
        Tool tool = createTool("Gata");

        ToolStatusDto dto = new ToolStatusDto();
        dto.setEmail(email);
        dto.setIdTool(tool.getIdTool());

        cardexService.saveCardexRepairTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream()
                .anyMatch(c -> c.getTypeMove().startsWith("Tool Repair status")));
    }

    @Test
    void saveCardexDeleteTool() {
        String email = "del@test.com";
        createUser(email);
        Tool tool = createTool("Clavos");

        ToolStatusDto dto = new ToolStatusDto();
        dto.setEmail(email);
        dto.setIdTool(tool.getIdTool());

        cardexService.saveCardexDeleteTool(dto, tool);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Tool Delete".equals(c.getTypeMove())));
    }

    @Test
    void saveCardexReturnLoan() {
        String email = "return@test.com";
        createUser(email);
        Tool tool = createTool("Taladro");
        Client client = createClient("44.444.444-4");
        Loan loan = createLoan(client, tool);

        ReturnLoanDto dto = new ReturnLoanDto();
        dto.setEmail(email);
        dto.setLoanId(loan.getLoanId());

        cardexService.saveCardexReturnLoan(dto);

        assertTrue(cardexRepositorie.findAll().stream().anyMatch(c -> "Loan Finished".equals(c.getTypeMove())));
    }

    @Test
    void saveCardexReturnLoanDamage() { // Test Faltante Agregado
        String email = "damage@test.com";
        createUser(email);
        Tool tool = createTool("Martillo Roto");
        Client client = createClient("55.555.555-5");
        Loan loan = createLoan(client, tool);

        ReturnLoanDto dto = new ReturnLoanDto();
        dto.setEmail(email);
        dto.setLoanId(loan.getLoanId());

        cardexService.saveCardexReturnLoanDamage(dto);

        List<Cardex> results = cardexRepositorie.findAll();
        assertTrue(results.stream().anyMatch(c -> c.getDescription().contains("damaged")));
    }

    @Test
    void saveCardexReturnLoanDelete() { // Test Faltante Agregado
        String email = "reploan@test.com";
        createUser(email);
        Tool tool = createTool("Sierra Perdida");
        Client client = createClient("66.666.666-6");
        Loan loan = createLoan(client, tool);

        ReturnLoanDto dto = new ReturnLoanDto();
        dto.setEmail(email);
        dto.setLoanId(loan.getLoanId());

        cardexService.saveCardexReturnLoanDelete(dto);

        List<Cardex> results = cardexRepositorie.findAll();
        assertTrue(results.stream().anyMatch(c -> c.getDescription().contains("replaced")));
    }

    @Test
    void findAllDto() {
        // Generar un dato para asegurar que la lista no venga vacía si la query lo requiere
        String email = "find@test.com";
        createUser(email);
        Tool tool = createTool("TestFind");
        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(email);
        dto.setQuantity(1);
        cardexService.saveCardexTool(dto, tool);

        List<CardexDto> dtos = cardexService.findAllDto();
        assertFalse(dtos.isEmpty());
    }

    @Test
    void findForRangeDate() {
        // Crear movimiento
        String email = "range@test.com";
        createUser(email);
        Tool tool = createTool("Rango");
        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(email);
        dto.setQuantity(1);
        cardexService.saveCardexTool(dto, tool);

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        List<CardexDto> dtos = cardexService.findForRangeDate(start, end, null);
        assertFalse(dtos.isEmpty());
    }

    @Test
    void findCardexTool() {
        String email = "toolfin@test.com";
        createUser(email);
        Tool tool = createTool("ToolSpecific");
        CreateToolDto dto = new CreateToolDto();
        dto.setEmail(email);
        dto.setQuantity(1);
        cardexService.saveCardexTool(dto, tool);

        List<CardexDto> dtos = cardexService.findCardexTool(tool.getIdTool());

        assertFalse(dtos.isEmpty());
        // Validamos que el primer elemento corresponde al tool ID (asumiendo que el DTO tiene ese campo)
        assertEquals(tool.getIdTool(), dtos.get(0).getToolId());
    }
}