package com.example.tingesoM.Service.ServiceImpl;

import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Service.Interface.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepositorie loanRepo;
    private final ToolRepositorie toolRepo;
    private final ClientRepositorie customerRepo;

    public LoanServiceImpl(LoanRepositorie loanRepo, ToolRepositorie toolRepo, ClientRepositorie customerRepo) {
        this.loanRepo = loanRepo;
        this.toolRepo = toolRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public Loan createLoan(String clientRut,Long toolId,LocalDate deliveryDate,LocalDate expectedReturnDate) {

        if (clientRut == null || toolId == null) {
            throw new IllegalArgumentException("clientId y toolId cannot be null");
        }

        Client client = customerRepo.findByRut(clientRut)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Tool tool = toolRepo.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found"));

        // RF2.5
        List<Loan> defaulter = loanRepo.findByClientAndLoanStatusFalseAndPenaltyTrue(client);
        if (!defaulter.isEmpty()) {
            throw new IllegalStateException("Client with outstanding fines ");
        }

        // RF2.2
        int stock = toolRepo.countAvailableByNameAndCategory(tool.getName(), tool.getCategory(), tool.getLoanFee());
        if (stock <= 0) {
            throw new IllegalStateException(
                    "Stock not available " + tool.getName());
        }

        // make loan
        Loan loan = new Loan();
        loan.setClient(client);
        loan.setTool(tool);
        loan.setDeliveryDate(deliveryDate);
        loan.setReturnDate(expectedReturnDate);
        loan.setLoanStatus(true);
        loan.setPenalty(false);
        loan.setPenaltyTotal(0);

        // change tool status to false
        tool.setStatus(false);
        toolRepo.save(tool);

        return loanRepo.save(loan);
    }

    @Override
    public Loan returnLoan(Long loanId, LocalDate actualReturnDate) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        if (!loan.getLoanStatus()) {
            throw new IllegalStateException("Préstamo ya devuelto");
        }

        // RF2.3: actualizar estado y calcular multa
        loan.setLoanStatus(false);

        long daysLate = ChronoUnit.DAYS.between(
                loan.getReturnDate(), actualReturnDate);
        if (daysLate > 0) {
            int multa = (int) (daysLate * loan.getTool().getPenaltyForDelay());
            loan.setPenalty(true);
            loan.setPenaltyTotal(multa);
        } else {
            loan.setPenalty(false);
            loan.setPenaltyTotal(0);
        }

        // liberar herramienta
        Tool tool = loan.getTool();
        tool.setStatus(true);
        toolRepo.save(tool);

        return loanRepo.save(loan);
    }

    /*
    public List<LoanResponseDto> findAll(){
        return loanRepo.findAllWithCustomerAndToolIds();
    }
    */


    @Override
    @Transactional
    public List<LoanResponseDto> findAll() {
        LocalDate today = LocalDate.now();

        // Recupera todas las loans; si quieres evitar N+1, crea un método que haga fetch join de tool
        List<Loan> allLoans = loanRepo.findAll();

        List<Loan> toSave = new ArrayList<>();

        for (Loan loan : allLoans) {
            // SOLO recalcular para préstamos activos (loanStatus == true)
            if (Boolean.TRUE.equals(loan.getLoanStatus())) {
                long daysLate = ChronoUnit.DAYS.between(loan.getReturnDate(), today);
                boolean hasPenalty = daysLate > 0;
                int totalPenalty = hasPenalty
                        ? (int) (daysLate * loan.getTool().getPenaltyForDelay())
                        : 0;
                if (!Objects.equals(loan.getPenalty(), hasPenalty) || !Objects.equals(loan.getPenaltyTotal(), totalPenalty)) {
                    loan.setPenalty(hasPenalty);
                    loan.setPenaltyTotal(totalPenalty);
                    loan.getClient().setStatus(Boolean.FALSE);
                    toSave.add(loan);
                }
            }
        }

        if (!toSave.isEmpty()) {
            loanRepo.saveAll(toSave);
        }

        return loanRepo.findAllWithClientAndToolIds();
    }

    @Override
    public List<Client> findClientDelayed(){
        return loanRepo.findClientsWithDelayedLoans();
    }

}
