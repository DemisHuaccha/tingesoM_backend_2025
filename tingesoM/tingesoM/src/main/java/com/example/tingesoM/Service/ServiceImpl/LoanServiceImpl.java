package com.example.tingesoM.Service.ServiceImpl;

import com.example.tingesoM.Dtos.CreateLoanRequestA;
import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Repositorie.UserRepositorie.ClientRepositorie;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Service.Interface.LoanService;
import com.example.tingesoM.Service.ServiceImpl.Users.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LoanRepositorie loanRepo;
    @Autowired
    private ToolRepositorie toolRepo;
    @Autowired
    private ClientRepositorie customerRepo;
    @Autowired
    private ClientServiceImpl clientService;


    @Override
    public Boolean isToolAvailableForClient(Long clientId, String toolName, String toolCategory, Integer loanFee) {
        return loanRepo.clientHasNoMatchingLoan(clientId, toolName, toolCategory, loanFee);
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

        if (tool.getStatus()==Boolean.FALSE || tool.getDeleteStatus()==Boolean.TRUE || tool.getUnderRepair()==Boolean.TRUE){
            throw new IllegalStateException("Tool is not available for loan");
        }
        // RF2.5
        List<Loan> defaulter = loanRepo.findByClientAndLoanStatusTrueAndPenaltyTrue(client);
        if (!defaulter.isEmpty()) {
            throw new IllegalStateException("Client with outstanding fines ");
        }

        // RF2.2
        int stock = toolRepo.countAvailableByNameAndCategory(tool.getName(), tool.getCategory(), tool.getLoanFee());
        if (stock <= 0) {
            throw new IllegalStateException(
                    "Stock not available " + tool.getName());
        }

        if (isToolAvailableForClient(client.getIdClient(), tool.getName(), tool.getCategory(), tool.getLoanFee())==Boolean.TRUE){
            throw new IllegalStateException("Client has a loan with same category, name and loan Fee tool ");
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
        loanRepo.save(loan);

        return loan;
    }

    /* Un used
    @Override
    public List<Loan> createLoanL(CreateLoanRequestA loanRequestA) {

        if (loanRequestA.getClientRut() == null || loanRequestA.getToolId().isEmpty()) {
            throw new IllegalArgumentException("clientId y toolId cannot be null.");
        }

        Client client = customerRepo.findByRut(loanRequestA.getClientRut())
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));

        // Verify that the client doesn't have any penalties on their active loans
        List<Loan> defaulter = loanRepo.findByClientAndLoanStatusTrueAndPenaltyTrue(client);
        if (!defaulter.isEmpty()) {
            throw new IllegalStateException("Client with outstanding fines. ");
        }

        List<Tool> listTool= new ArrayList<>();

        for(int i=0; i<loanRequestA.getToolId().size() ; i++){
            Long toolId = loanRequestA.getToolId().get(i);
            Tool tool = toolRepo.findById(toolId).orElseThrow(() -> new IllegalArgumentException("Tool not found."));
            listTool.add(tool);
        }

        //Verify that the tools do not have the same category and that each has a valid stock
        for(int i=0; i<listTool.size() ; i++){
            if(listTool.get(i).getStatus()==Boolean.FALSE){
                throw new IllegalStateException("The tool is not available for loan. ");
            }
            for(int s=i+1; s<listTool.size() ; s++) {
                if (listTool.get(i).getCategory().equals(listTool.get(s).getCategory())) {
                    throw new IllegalStateException("There are two tools with the same category. ");
                }
            }


            int stock = toolRepo.countAvailableByNameAndCategory(listTool.get(i).getName(), listTool.get(i).getCategory(), listTool.get(i).getLoanFee());
            if (stock <= 0) {
                throw new IllegalStateException("Stock not available for:" + listTool.get(i).getName());
            }
        }


        // make loans

        List<Loan> listLoan= new ArrayList<>();

        for(int i=0; i<listTool.size() ; i++){
            Tool tool = listTool.get(i);
            //Make Loan
            Loan loan = new Loan();
            loan.setClient(client);
            loan.setTool(tool);
            loan.setDeliveryDate(loanRequestA.getDeliveryDate());
            loan.setReturnDate(loanRequestA.getReturnDate());
            loan.setLoanStatus(true);
            loan.setPenalty(false);
            loan.setPenaltyTotal(0);

            // change tool status to false
            tool.setStatus(false);
            toolRepo.save(tool);
            loanRepo.save(loan);
            listLoan.add(loan);
        }

        return listLoan;
    }
*/


    @Override
    public Loan returnLoan(Long loanId, LocalDate actualReturnDate) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (!loan.getLoanStatus()) {
            throw new IllegalStateException("Loan already returned");
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
        loan.setPriceToPay(loan.getPriceToPay());
        loanRepo.save(loan);
        loan.getClient().setStatus(Boolean.TRUE);
        return loanRepo.save(loan);
    }

    @Override
    public Loan returnLoanDamageTool(Long loanId, LocalDate actualReturnDate) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (!loan.getLoanStatus()) {
            throw new IllegalStateException("Loan already returned");
        }

        loan.setLoanStatus(false);

        long daysLate = ChronoUnit.DAYS.between(loan.getReturnDate(), actualReturnDate);

        // CORRECCIÓN: Calcular multa por atraso (si existe)
        int delayPenalty = (daysLate > 0) ? (int) (daysLate * loan.getTool().getPenaltyForDelay()) : 0;

        // CORRECCIÓN: Sumar SIEMPRE el valor del daño
        int totalPenalty = delayPenalty + loan.getTool().getDamageValue();

        // Siempre hay penalidad porque hubo daño
        loan.setPenalty(true);
        loan.setPenaltyTotal(totalPenalty);

        // ... resto de lógica de liberar herramienta ...
        Tool tool = loan.getTool();
        tool.setStatus(false);
        tool.setUnderRepair(true);
        toolRepo.save(tool);

        // Asegúrate de que getPriceToPay() no sea null (como corregimos antes)
        int currentPrice = loan.getPriceToPay() != null ? loan.getPriceToPay() : 0;
        loan.setPriceToPay(currentPrice + loan.getTool().getDamageValue());

        loan.getClient().setStatus(Boolean.TRUE);
        return loanRepo.save(loan);
    }

    @Override
    public Loan returnLoanDeleteTool(Long loanId, LocalDate actualReturnDate) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (!loan.getLoanStatus()) {
            throw new IllegalStateException("Loan already returned");
        }

        loan.setLoanStatus(false);

        long daysLate = ChronoUnit.DAYS.between(loan.getReturnDate(), actualReturnDate);

        // CORRECCIÓN: Calcular multa por atraso (si existe)
        int delayPenalty = (daysLate > 0) ? (int) (daysLate * loan.getTool().getPenaltyForDelay()) : 0;

        // CORRECCIÓN: Sumar SIEMPRE el valor de reposición
        int totalPenalty = delayPenalty + loan.getTool().getReplacementValue();

        // Siempre hay penalidad porque se perdió/eliminó
        loan.setPenalty(true);
        loan.setPenaltyTotal(totalPenalty);

        // ... resto de lógica de herramienta ...
        Tool tool = loan.getTool();
        tool.setStatus(false);
        tool.setUnderRepair(false);
        tool.setDeleteStatus(true); // OJO: Verifica si esto debe ser true o false según tu lógica
        toolRepo.save(tool);

        int currentPrice = loan.getPriceToPay() != null ? loan.getPriceToPay() : 0;
        loan.setPriceToPay(currentPrice + loan.getTool().getReplacementValue());

        loan.getClient().setStatus(Boolean.TRUE);
        return loanRepo.save(loan);
    }


    @Override
    @Transactional
    public List<LoanResponseDto> findAll() {
        LocalDate today = LocalDate.now();
        List<Loan> allLoans = loanRepo.findAll();

        List<Loan> toSave = new ArrayList<>();

        for (Loan loan : allLoans) {
            // Solo re-calcula para préstamos activos
            if (Boolean.TRUE.equals(loan.getLoanStatus())) {
                long daysLate = ChronoUnit.DAYS.between(loan.getReturnDate(), today);
                long daysloanfee = ChronoUnit.DAYS.between(loan.getDeliveryDate(), loan.getReturnDate());
                int daysloanfeeInt = (int) daysloanfee;
                boolean hasPenalty = daysLate > 0;
                int totalPenalty = hasPenalty
                        ? (int) (daysLate * loan.getTool().getPenaltyForDelay())
                        : 0;
                int newPriceToPay = (loan.getTool().getLoanFee() * daysloanfeeInt) + totalPenalty;
                loan.setPriceToPay(newPriceToPay);
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

        clientService.restrictClientsWithDelayedLoans();
        return loanRepo.findAllWithClientAndToolIds();
    }



}
