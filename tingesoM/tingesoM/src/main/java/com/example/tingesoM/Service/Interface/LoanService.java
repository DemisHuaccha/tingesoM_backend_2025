package com.example.tingesoM.Service.Interface;

import com.example.tingesoM.Dtos.CreateLoanRequestA;
import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {
    Loan createLoan(String clientId, Long toolId, LocalDate deliveryDate, LocalDate expectedReturnDate);

    Loan returnLoan(Long loanId, LocalDate actualReturnDate);

    List<LoanResponseDto> findAll();

    List<Client> findClientDelayed();

    Loan returnLoanDamageTool(Long loanId, LocalDate actualReturnDate);

    Loan returnLoanDeleteTool(Long loanId, LocalDate actualReturnDate);

    /*
    List<Loan> createLoanL(CreateLoanRequestA loanRequestA);
    */

    Boolean isToolAvailableForClient(Long clientId, String toolName, String toolCategory, Integer loanFee);
}
