package com.example.tingesoM.Service;

import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Service.ServiceImpl.LoanServiceImpl;
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
class LoanServiceImplTest {

    @Autowired
    private LoanServiceImpl loanService;

    @Autowired
    private LoanRepositorie loanRepo;

    @Autowired
    private ToolRepositorie toolRepo;

    @Autowired
    private ClientRepositorie clientRepo;

    private static Long loanId;
    private static Long toolId;
    private static String clientRut = "12.345.678-9";

    @BeforeAll
    static void setup(@Autowired ToolRepositorie toolRepo,
                      @Autowired ClientRepositorie clientRepo,
                      @Autowired LoanRepositorie loanRepo) {

        Tool tool = new Tool();
        tool.setName("Taladro");
        tool.setCategory("Electricas");
        tool.setLoanFee(10000);
        tool.setPenaltyForDelay(500);
        tool.setStatus(true);
        tool.setUnderRepair(false);
        tool.setDeleteStatus(false);
        toolRepo.save(tool);
        toolId = tool.getIdTool();

        Client client = new Client();
        client.setFirstName("Ana");
        client.setLastName("Torres");
        client.setRut(clientRut);
        client.setEmail("ana@example.com");
        client.setPhone("+56912345678");
        client.setStatus(true);
        clientRepo.save(client);
    }

    @Test
    @Order(1)
    void createLoan() {
        LocalDate deliveryDate = LocalDate.now().minusDays(2);
        LocalDate returnDate = LocalDate.now().plusDays(3);

        Loan loan = loanService.createLoan(clientRut, toolId, deliveryDate, returnDate);
        loanId = loan.getLoanId();

        assertNotNull(loan);
        assertEquals(clientRut, loan.getClient().getRut());
        assertEquals(toolId, loan.getTool().getIdTool());
        assertTrue(loan.getLoanStatus());
    }

    @Test
    @Order(2)
    void returnLoan() {
        LocalDate actualReturnDate = LocalDate.now().plusDays(5); // simula retraso

        Loan returned = loanService.returnLoan(loanId, actualReturnDate);

        assertFalse(returned.getLoanStatus());
        assertTrue(returned.getPenalty());
        assertTrue(returned.getPenaltyTotal() > 0);
        assertTrue(returned.getTool().getStatus()); // herramienta liberada
    }

    @Test
    @Order(3)
    void findAll() {
        List<LoanResponseDto> loans = loanService.findAll();
        assertFalse(loans.isEmpty());
        assertEquals(clientRut, loans.get(0).getClientRut());
    }

    @Test
    @Order(4)
    void findClientDelayed() {
        List<Client> delayed = loanService.findClientDelayed();
        assertFalse(delayed.isEmpty());
        assertEquals(clientRut, delayed.get(0).getRut());
    }
}
