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
class LoanServiceImplTest {

    @Autowired
    private LoanServiceImpl loanService;

    @Autowired
    private LoanRepositorie loanRepo;

    @Autowired
    private ToolRepositorie toolRepo;

    @Autowired
    private ClientRepositorie clientRepo;

    // --- Métodos Helper para crear datos rápidamente ---

    private Client createClient(String rut) {
        Client client = new Client();
        client.setFirstName("Test");
        client.setLastName("User");
        client.setRut(rut);
        client.setEmail("test" + rut + "@example.com");
        client.setPhone("123456789");
        client.setStatus(true);
        return clientRepo.save(client);
    }

    private Tool createTool(String name, String category, int fee) {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setCategory(category);
        tool.setLoanFee(fee); // Costo diario
        tool.setPenaltyForDelay(500); // Multa diaria
        tool.setDamageValue(2000);    // Costo reparación
        tool.setReplacementValue(5000); // Costo reposición
        tool.setStatus(true);         // Disponible
        tool.setUnderRepair(false);
        tool.setDeleteStatus(false);
        return toolRepo.save(tool);
    }

    private Loan createLoanInDb(Client client, Tool tool, LocalDate delivery, LocalDate returns) {
        Loan loan = new Loan();
        loan.setClient(client);
        loan.setTool(tool);
        loan.setDeliveryDate(delivery);
        loan.setReturnDate(returns);
        loan.setLoanStatus(true);
        loan.setPenalty(false);
        loan.setPenaltyTotal(0);

        // --- AGREGA ESTA LÍNEA ---
        loan.setPriceToPay(0);
        // -------------------------

        // Al crear el préstamo manualmente, actualizamos la herramienta a "ocupada"
        tool.setStatus(false);
        toolRepo.save(tool);

        return loanRepo.save(loan);
    }

    // --- TESTS ---

    @Test
    void isToolAvailableForClient() {
        Client client = createClient("11.111.111-1");
        Tool tool = createTool("Sierra", "Manual", 1000);

        // CASO 1: Cliente NO tiene préstamos previos.
        // La query hace COUNT >= 1. Como hay 0, retorna FALSE.
        // Interpretación: FALSE significa "No hay duplicados, puede pedir prestado".
        Boolean result = loanService.isToolAvailableForClient(
                client.getIdCustomer(),
                tool.getName(),
                tool.getCategory(),
                tool.getLoanFee()
        );

        // Esperamos FALSE porque el cliente está "limpio"
        assertFalse(result, "Debería ser FALSE indicando que NO se encontró ningún préstamo activo previo");


        // CASO 2: Cliente YA TIENE 1 préstamo con las mismas características.
        createLoanInDb(client, tool, LocalDate.now(), LocalDate.now().plusDays(5));

        // Ahora la query encuentra 1. COUNT(1) >= 1 es TRUE.
        // Interpretación: TRUE significa "Ya existe un préstamo, NO puede pedir otro igual".
        Boolean resultAfter = loanService.isToolAvailableForClient(
                client.getIdCustomer(),
                tool.getName(),
                tool.getCategory(),
                tool.getLoanFee()
        );

        // Esperamos TRUE porque el sistema detectó la duplicidad
        assertTrue(resultAfter, "Debería ser TRUE indicando que SÍ existe un préstamo activo (conflicto)");
    }

    @Test
    void createLoan() {
        Client client = createClient("22.222.222-2");
        Tool tool = createTool("Martillo", "Manual", 500);

        Loan loan = loanService.createLoan(client.getRut(), tool.getIdTool(), LocalDate.now(), LocalDate.now().plusDays(3));

        assertNotNull(loan);
        assertNotNull(loan.getLoanId());
        assertEquals(client.getRut(), loan.getClient().getRut());
        assertFalse(toolRepo.findById(tool.getIdTool()).get().getStatus(), "La herramienta debe quedar marcada como NO disponible");
    }

    @Test
    void returnLoan() {
        Client client = createClient("44.444.444-4");
        Tool tool = createTool("Llave", "Manual", 1000);
        // Préstamo que venció hace 5 días
        Loan loan = createLoanInDb(client, tool, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));

        // Devolución HOY (con 5 días de retraso)
        Loan returned = loanService.returnLoan(loan.getLoanId(), LocalDate.now());

        assertFalse(returned.getLoanStatus(), "El préstamo debe estar finalizado");
        assertTrue(returned.getPenalty(), "Debe tener multa por atraso");
        assertTrue(returned.getPenaltyTotal() > 0, "El total de multa debe ser mayor a 0");
        assertTrue(toolRepo.findById(tool.getIdTool()).get().getStatus(), "La herramienta debe volver a estar disponible");
    }

    @Test
    void returnLoanDamageTool() {
        Client client = createClient("55.555.555-5");
        Tool tool = createTool("Lijadora", "Electrica", 3000); // DamageValue = 2000
        Loan loan = createLoanInDb(client, tool, LocalDate.now().minusDays(2), LocalDate.now().plusDays(2)); // A tiempo

        // Devolución con daño
        Loan returned = loanService.returnLoanDamageTool(loan.getLoanId(), LocalDate.now());

        // Verificar que se cobra el daño
        // Nota: Si no hubo atraso, multa = 0 + damageValue
        assertEquals(tool.getDamageValue(), returned.getPenaltyTotal(), "La multa debe ser igual al valor del daño si no hubo atraso");
        assertTrue(toolRepo.findById(tool.getIdTool()).get().getUnderRepair(), "La herramienta debe quedar en reparación");
    }

    @Test
    void returnLoanDeleteTool() {
        Client client = createClient("66.666.666-6");
        Tool tool = createTool("Generador", "Motor", 50000); // Replacement = 5000
        Loan loan = createLoanInDb(client, tool, LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));

        // Devolución por pérdida/eliminación
        Loan returned = loanService.returnLoanDeleteTool(loan.getLoanId(), LocalDate.now());

        assertEquals(tool.getReplacementValue(), returned.getPenaltyTotal());
        // Validamos la lógica actual de tu servicio:
        assertFalse(toolRepo.findById(tool.getIdTool()).get().getDeleteStatus(), "Según tu servicio, deleteStatus se pone en false");
        assertFalse(toolRepo.findById(tool.getIdTool()).get().getStatus(), "La herramienta queda como no disponible");
    }

    @Test
    void findAll() {
        Client client = createClient("77.777.777-7");
        Tool tool = createTool("Calculadora", "Oficina", 100);

        // Creamos un préstamo activo vencido
        Loan loan = createLoanInDb(client, tool, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));

        // Al ejecutar findAll, tu servicio itera y recalcula las multas para los prestamos activos
        List<LoanResponseDto> allLoans = loanService.findAll();

        assertFalse(allLoans.isEmpty());

        // Verificamos en DB que se haya actualizado la multa automáticamente
        Loan updatedLoan = loanRepo.findById(loan.getLoanId()).get();
        assertTrue(updatedLoan.getPenalty(), "findAll debería haber detectado el atraso y puesto penalty=true");
        assertTrue(updatedLoan.getPenaltyTotal() > 0, "findAll debería haber calculado el monto de la multa");
    }

    @Test
    void findClientDelayed() {
        Client client = createClient("88.888.888-8");
        Tool tool = createTool("Sierra", "Manual", 500);

        // Creamos el préstamo vencido
        Loan loan = createLoanInDb(client, tool, LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));

        // CORRECCIÓN: Como tu Query busca "WHERE penalty = true", debemos setearlo manualmente
        // en el test para simular que el sistema ya detectó el atraso.
        loan.setPenalty(true);
        loanRepo.save(loan);

        List<Client> delayedClients = loanService.findClientDelayed();

        assertFalse(delayedClients.isEmpty());
        assertEquals(client.getRut(), delayedClients.get(0).getRut());
    }
}