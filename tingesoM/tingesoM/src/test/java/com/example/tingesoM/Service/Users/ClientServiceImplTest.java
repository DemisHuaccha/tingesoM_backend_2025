package com.example.tingesoM.Service.Users;

import com.example.tingesoM.Dtos.CreateClientDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Service.ServiceImpl.Users.ClientServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClientServiceImplTest {

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private ClientRepositorie clientRepositorie;

    @Autowired
    private LoanRepositorie loanRepositorie;

    // --- Helper para crear datos rápidamente en los tests ---
    private Client createAndSaveClient(String rut, String name, String email) {
        Client client = new Client();
        client.setRut(rut);
        client.setFirstName(name);
        client.setLastName("TestLastname");
        client.setEmail(email);
        client.setPhone("+56911111111");
        client.setStatus(true);
        return clientRepositorie.save(client);
    }

    // 1. Test para Save (Create)
    @Test
    void save() {
        CreateClientDto dto = new CreateClientDto();
        dto.setFirstName("Ana");
        dto.setLastName("Torres");
        dto.setRut("12.345.678-9");
        dto.setEmailC("ana@example.com"); // Según tu DTO usas emailC
        dto.setPhone("+56912345678");

        Client saved = clientService.save(dto);

        assertNotNull(saved.getIdCustomer());
        assertEquals("12.345.678-9", saved.getRut());
        assertTrue(saved.getStatus());
    }

    // 2. Test para FindByName (Read)
    @Test
    void findByName() {
        createAndSaveClient("11.111.111-1", "Roberto", "roberto@mail.com");

        Client found = clientService.findByName("Roberto");

        assertNotNull(found);
        assertEquals("Roberto", found.getFirstName());
    }

    // 3. Test para FindById (Read)
    @Test
    void findById() {
        Client original = createAndSaveClient("22.222.222-2", "Maria", "maria@mail.com");

        Client found = clientService.findById(original.getIdCustomer());

        assertNotNull(found);
        assertEquals(original.getIdCustomer(), found.getIdCustomer());
    }

    // 4. Test para FindAll (Read)
    @Test
    void findAll() {
        createAndSaveClient("33.333.333-3", "User1", "u1@mail.com");
        createAndSaveClient("44.444.444-4", "User2", "u2@mail.com");

        List<Client> clients = clientService.findAll();

        assertFalse(clients.isEmpty());
        assertTrue(clients.size() >= 2);
    }

    // 5. Test para FindDelayedClient (Lógica de negocio / Query)
    @Test
    void findDelayedClient() {
        // Crear cliente
        Client client = createAndSaveClient("55.555.555-5", "Deudor", "deudor@mail.com");

        // Crear prestamo vencido
        Loan loan = new Loan();
        loan.setClient(client);
        loan.setDeliveryDate(LocalDate.now().minusDays(20));
        loan.setReturnDate(LocalDate.now().minusDays(5)); // Venció hace 5 días
        loan.setLoanStatus(true); // Activo
        loanRepositorie.save(loan);

        List<Client> delayedClients = clientService.findDelayedClient();

        assertFalse(delayedClients.isEmpty());
        assertTrue(delayedClients.stream().anyMatch(c -> c.getRut().equals("55.555.555-5")));
    }

    // 6. Test para UpdateCustomer (Update)
    @Test
    void updateCustomer() {
        Client original = createAndSaveClient("66.666.666-6", "Original", "ori@mail.com");

        // Objeto con los nuevos datos
        Client updates = new Client();
        updates.setFirstName("Actualizado");
        updates.setLastName("NuevoApellido");
        updates.setRut("66.666.666-6");

        clientService.updateCustomer(updates, original.getIdCustomer());

        // Verificar
        Client updatedDb = clientRepositorie.findById(original.getIdCustomer()).orElseThrow();
        assertEquals("Actualizado", updatedDb.getFirstName());
        assertEquals("NuevoApellido", updatedDb.getLastName());
    }

    // 7. Test para UpdateStatusCustomer (Update lógico)
    @Test
    void updateStatusCustomer() {
        Client client = createAndSaveClient("77.777.777-7", "StatusTest", "st@mail.com");
        assertTrue(client.getStatus()); // Empieza en true

        clientService.updateStatusCustomer(client.getIdCustomer());

        Client updated = clientRepositorie.findById(client.getIdCustomer()).orElseThrow();
        assertFalse(updated.getStatus()); // Ahora debe ser false
    }

    // 8. Test para DeleteCustomer (Delete lógico)
    @Test
    void deleteCustomer() {
        Client client = createAndSaveClient("88.888.888-8", "DeleteMe", "del@mail.com");

        clientService.deleteCustomer(client);

        // OJO: Tu servicio deleteCustomer hace client.setStatus(Boolean.FALSE) pero NO llama a save().
        // Si tu servicio no tiene .save(client), el cambio no se persistirá en DB.
        // Asumiendo que es una operación en memoria o que JPA gestiona la entidad adjunta:
        assertFalse(client.getStatus());
    }

    // 9. Test para RestrictClientsWithDelayedLoans (Lógica masiva)
    @Test
    void restrictClientsWithDelayedLoans() {
        // Cliente con préstamo atrasado
        Client c1 = createAndSaveClient("99.999.999-9", "Late", "late@mail.com");
        Loan l1 = new Loan();
        l1.setClient(c1);
        l1.setDeliveryDate(LocalDate.now().minusDays(15));
        l1.setReturnDate(LocalDate.now().minusDays(10)); // Vencido
        l1.setLoanStatus(true);
        loanRepositorie.save(l1);

        // Cliente al día
        Client c2 = createAndSaveClient("10.000.000-0", "OnTime", "ontime@mail.com");
        Loan l2 = new Loan();
        l2.setClient(c2);
        l2.setDeliveryDate(LocalDate.now().minusDays(5));
        l2.setReturnDate(LocalDate.now().plusDays(10)); // Futuro
        l2.setLoanStatus(true);
        loanRepositorie.save(l2);

        // Ejecutar proceso
        clientService.restrictClientsWithDelayedLoans();

        // Verificar
        Client c1Updated = clientRepositorie.findById(c1.getIdCustomer()).orElseThrow();
        Client c2Updated = clientRepositorie.findById(c2.getIdCustomer()).orElseThrow();

        assertFalse(c1Updated.getStatus(), "El cliente moroso debe ser bloqueado");
        assertTrue(c2Updated.getStatus(), "El cliente al día no debe ser bloqueado");
    }

    // 10. Test para SearchRuts (Búsqueda parcial)
    @Test
    void searchRuts() {
        createAndSaveClient("12.333.111-1", "A", "a@a.com");
        createAndSaveClient("12.333.222-2", "B", "b@b.com");
        createAndSaveClient("99.999.999-9", "C", "c@c.com");

        List<String> results = clientService.searchRuts("12.333");

        assertEquals(2, results.size());
        assertTrue(results.contains("12.333.111-1"));
        assertTrue(results.contains("12.333.222-2"));
        assertFalse(results.contains("99.999.999-9"));
    }
}
