package com.example.tingesoM.Service;

import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Service.ServiceImpl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    @Mock
    private LoanRepositorie loanRepositorie;

    @InjectMocks
    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getActiveLoans_WithDates_ReturnsFilteredLoans() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        Loan loan = new Loan();
        when(loanRepositorie.findActiveLoansByDateRange(start, end)).thenReturn(Collections.singletonList(loan));

        List<Loan> result = reportService.getActiveLoans(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findActiveLoansByDateRange(start, end);
    }

    @Test
    void getActiveLoans_WithoutDates_ReturnsAllActiveLoans() {
        Loan loan = new Loan();
        when(loanRepositorie.findActiveAndOnTimeLoans()).thenReturn(Collections.singletonList(loan));

        List<Loan> result = reportService.getActiveLoans(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findActiveAndOnTimeLoans();
    }

    @Test
    void getOverdueLoans_WithDates_ReturnsFilteredLoans() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        Loan loan = new Loan();
        when(loanRepositorie.findOverdueLoansByDateRange(start, end)).thenReturn(Collections.singletonList(loan));

        List<Loan> result = reportService.getOverdueLoans(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findOverdueLoansByDateRange(start, end);
    }

    @Test
    void getOverdueLoans_WithoutDates_ReturnsAllOverdueLoans() {
        Loan loan = new Loan();
        when(loanRepositorie.findActiveAndDelayedLoans()).thenReturn(Collections.singletonList(loan));

        List<Loan> result = reportService.getOverdueLoans(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findActiveAndDelayedLoans();
    }

    @Test
    void getClientsWithOverdueLoans_WithDates_ReturnsFilteredClients() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        Client client = new Client();
        when(loanRepositorie.findClientsWithOverdueLoansByDateRange(start, end)).thenReturn(Collections.singletonList(client));

        List<Client> result = reportService.getClientsWithOverdueLoans(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findClientsWithOverdueLoansByDateRange(start, end);
    }

    @Test
    void getClientsWithOverdueLoans_WithoutDates_ReturnsAllClientsWithOverdueLoans() {
        Client client = new Client();
        when(loanRepositorie.findClientsWithDelayedLoans()).thenReturn(Collections.singletonList(client));

        List<Client> result = reportService.getClientsWithOverdueLoans(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findClientsWithDelayedLoans();
    }

    @Test
    void getToolRanking_WithDates_ReturnsFilteredRanking() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        ToolRankingDto ranking = new ToolRankingDto("Hammer", "Tools", 100, 5L);
        when(loanRepositorie.findToolRankingByDateRange(start, end)).thenReturn(Collections.singletonList(ranking));

        List<ToolRankingDto> result = reportService.getToolRanking(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findToolRankingByDateRange(start, end);
    }

    @Test
    void getToolRanking_WithoutDates_ReturnsAllRanking() {
        ToolRankingDto ranking = new ToolRankingDto("Hammer", "Tools", 100, 5L);
        when(loanRepositorie.findMostLoanedToolsWithDetails()).thenReturn(Collections.singletonList(ranking));

        List<ToolRankingDto> result = reportService.getToolRanking(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepositorie).findMostLoanedToolsWithDetails();
    }
}
