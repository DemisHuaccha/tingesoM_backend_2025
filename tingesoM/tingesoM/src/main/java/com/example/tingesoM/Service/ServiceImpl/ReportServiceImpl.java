package com.example.tingesoM.Service.ServiceImpl;

import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Service.Interface.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private LoanRepositorie loanRepositorie;

    @Override
    public List<Loan> getActiveLoans(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return loanRepositorie.findActiveAndOnTimeLoans();
        }
        return loanRepositorie.findActiveLoansByDateRange(startDate, endDate);
    }

    @Override
    public List<Loan> getOverdueLoans(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return loanRepositorie.findActiveAndDelayedLoans();
        }
        return loanRepositorie.findOverdueLoansByDateRange(startDate, endDate);
    }

    @Override
    public List<Client> getClientsWithOverdueLoans(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return loanRepositorie.findClientsWithDelayedLoans();
        }
        return loanRepositorie.findClientsWithOverdueLoansByDateRange(startDate, endDate);
    }

    @Override
    public List<ToolRankingDto> getToolRanking(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return loanRepositorie.findMostLoanedToolsWithDetails();
        }
        return loanRepositorie.findToolRankingByDateRange(startDate, endDate);
    }
}
