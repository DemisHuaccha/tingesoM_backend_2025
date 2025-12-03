package com.example.tingesoM.Controller;

import com.example.tingesoM.Dtos.CreateLoanRequest;
import com.example.tingesoM.Dtos.CreateLoanRequestA;
import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Dtos.ReturnLoanDto;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Service.ServiceImpl.CardexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.tingesoM.Service.ServiceImpl.LoanServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8070")
@RestController
@RequestMapping("/api/Loan")
public class LoanController {

    @Autowired
    private LoanServiceImpl loanService;

    @Autowired
    private CardexServiceImpl cardexServiceImpl;


    /*
    RF2.1 y RF2.2: registra un préstamo asociando cliente y herramienta,valida disponibilidad (stock por nombre y categoría).
     */
    @PostMapping("/createLoan")
    public ResponseEntity<Loan> createLoan(@RequestBody CreateLoanRequest loan){
        String clientRut = loan.getClientRut();
        Long toolId= loan.getToolId();
        LocalDate deliveryDate= loan.getDeliveryDate();
        LocalDate expectedReturnDate= loan.getReturnDate();

        Loan loanSave= loanService.createLoan(clientRut,toolId,deliveryDate,expectedReturnDate);
        cardexServiceImpl.saveCardexLoan(toolId, loan.getEmail(), loanSave);
        return ResponseEntity.ok(loanSave);
    }





    /*
    RF2.3, RF2.4 y RF2.5: registra devolución de herramienta,calcula multas por atraso y libera stock.
     */
    @PutMapping("/return")
    public ResponseEntity<Loan> returnLoan(@RequestBody ReturnLoanDto loanDto) {
        LocalDate date = LocalDate.now();
        Loan loan = loanService.returnLoan(loanDto.getLoanId(), date);
        cardexServiceImpl.saveCardexReturnLoan(loanDto);
        return ResponseEntity.ok(loan);
    }

    @PutMapping("/returnDamegeTool")
    public ResponseEntity<Loan> returnLoanDamage(@RequestBody ReturnLoanDto loanDto) {
        LocalDate date = LocalDate.now();
        Loan loan = loanService.returnLoanDamageTool(loanDto.getLoanId(), date);
        cardexServiceImpl.saveCardexReturnLoanDamage(loanDto);
        return ResponseEntity.ok(loan);
    }

    @PutMapping("/returnDeleteTool")
    public ResponseEntity<Loan> returnLoanDelete(@RequestBody ReturnLoanDto loanDto) {
        LocalDate date = LocalDate.now();
        Loan loan = loanService.returnLoanDeleteTool(loanDto.getLoanId(), date);
        cardexServiceImpl.saveCardexReturnLoanDelete(loanDto);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<LoanResponseDto> loans = loanService.findAll();
        return ResponseEntity.ok(loans);
    }
}

