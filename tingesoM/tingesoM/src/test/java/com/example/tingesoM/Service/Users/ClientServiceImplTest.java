package com.example.tingesoM.Service.Users;

import com.example.tingesoM.Dtos.CreateClientDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Service.ServiceImpl.Users.ClientServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientServiceImplTest {

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private ClientRepositorie clientRepositorie;

    @Autowired
    private LoanRepositorie loanRepositorie;

    private static Long clientId;

    @Test
    @Order(1)
    void save() {
        CreateClientDto dto = new CreateClientDto();
        dto.setFirstName("Ana");
        dto.setLastName("Torres");
        dto.setRut("12.345.678-9");
        dto.setEmailC("ana@example.com");
        dto.setPhone("+56912345678");

        Client saved = clientService.save(dto);
        clientId = saved.getIdCustomer();

        assertNotNull(saved);
        assertEquals("Ana", saved.getFirstName());
    }

    @Test
    @Order(2)
    void findByName() {
        Client client = clientService.findByName("Ana");
        assertNotNull(client);
        assertEquals("Ana", client.getFirstName());
    }

    @Test
    @Order(3)
    void findById() {
        Client found = clientService.findById(clientId);
        assertNotNull(found);
        assertEquals("Ana", found.getFirstName());
    }

    @Test
    @Order(4)
    void findAll() {
        List<Client> clients = clientService.findAll();
        assertFalse(clients.isEmpty());
    }

    @Test
    @Order(5)
    void updateStatusCustomer() {
        Client client = clientService.findById(clientId);
        Boolean oldStatus = client.getStatus();

        clientService.updateStatusCustomer(clientId);

        Client updated = clientService.findById(clientId);
        assertNotEquals(oldStatus, updated.getStatus());
    }

    @Test
    @Order(6)
    void deleteCustomer() {
        Client client = clientService.findById(clientId);
        clientService.deleteCustomer(client);
        Client deleted = clientService.findById(clientId);
        assertFalse(deleted.getStatus());
    }

    @Test
    @Order(7)
    void findDelayedClient() {
        // Crear cliente
        CreateClientDto dto = new CreateClientDto();
        dto.setFirstName("Carlos");
        dto.setLastName("Vega");
        dto.setRut("11.111.111-1");
        dto.setEmailC("carlos@example.com");
        dto.setPhone("+56987654321");

        Client delayedClient = clientService.save(dto);

        // Crear préstamo activo y vencido
        Loan loan = new Loan();
        loan.setClient(delayedClient);
        loan.setDeliveryDate(LocalDate.now().minusDays(20));
        loan.setReturnDate(LocalDate.now().minusDays(5)); // Ya venció
        loan.setLoanStatus(true); // Activo

        loanRepositorie.save(loan);

        // Ejecutar lógica
        List<Client> delayed = clientService.findDelayedClient();

        // Verificar que el cliente aparece como atrasado
        assertTrue(delayed.stream().anyMatch(c -> c.getRut().equals("11.111.111-1")));
    }


        @Test
    @Order(8)
    void updateCustomer() {
        Client client = clientService.findById(clientId);
        client.setFirstName("Ana María");
        clientService.updateCustomer(client, clientId);

        Client updated = clientService.findById(clientId);
        assertEquals("Ana María", updated.getFirstName());
    }
}
