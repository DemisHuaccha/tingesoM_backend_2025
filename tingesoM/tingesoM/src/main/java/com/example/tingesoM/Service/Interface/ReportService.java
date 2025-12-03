package com.example.tingesoM.Service.Interface;

import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<Loan> getActiveLoans(LocalDate startDate, LocalDate endDate);
    List<Loan> getOverdueLoans(LocalDate startDate, LocalDate endDate);
    List<Client> getClientsWithOverdueLoans(LocalDate startDate, LocalDate endDate);
    List<ToolRankingDto> getToolRanking(LocalDate startDate, LocalDate endDate);
}
