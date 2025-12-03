package com.example.tingesoM.Repositorie;

import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepositorie extends JpaRepository<Loan,Long> {

    List<Loan> findByClientAndLoanStatusTrue(Client client);

    List<Loan> findByClientAndLoanStatusTrueAndPenaltyTrue(Client client);


    @Query("""
            SELECT new com.example.tingesoM.Dtos.LoanResponseDto(
            l.loanId, l.deliveryDate, l.returnDate, l.loanStatus, l.penalty, l.penaltyTotal, l.client.rut, l.tool.idTool, l.priceToPay)
            FROM Loan l
            """)
    List<LoanResponseDto> findAllWithClientAndToolIds();

    //Return loan actives
    //Return loan actives (On Time)
    @Query("SELECT l FROM Loan l WHERE l.loanStatus = true AND (l.returnDate >= CURRENT_DATE OR l.returnDate IS NULL)")
    List<Loan> findActiveAndOnTimeLoans();

    //Return loan actives and delayed
    @Query("SELECT l FROM Loan l WHERE l.loanStatus = true AND l.returnDate < CURRENT_DATE")
    List<Loan> findActiveAndDelayedLoans();

    //Return clients with loans delayed
    @Query("SELECT DISTINCT l.client FROM Loan l WHERE l.penalty = true")
    List<Client> findClientsWithDelayedLoans();

    //Ranking
    @Query("SELECT new com.example.tingesoM.Dtos.ToolRankingDto( " +
            "l.tool.name, l.tool.category, l.tool.loanFee, COUNT(l)) " +
            "FROM Loan l " +
            "GROUP BY l.tool.name, l.tool.category, l.tool.loanFee " +
            "ORDER BY COUNT(l) DESC")
    List<ToolRankingDto> findMostLoanedToolsWithDetails();

    @Query("SELECT l.loanId || ' - ' || l.tool.name FROM Loan l")
    List<String> debugRanking();


    @Query("SELECT COUNT(l) >= 1 FROM Loan l " +
            "WHERE l.client.idClient = :clientId " +
            "AND l.loanStatus = true " +
            "AND l.tool.name = :toolName " +
            "AND l.tool.category = :toolCategory " +
            "AND l.tool.loanFee = :toolLoanFee")
    boolean clientHasNoMatchingLoan(@Param("clientId") Long clientId,
                                    @Param("toolName") String toolName,
                                    @Param("toolCategory") String toolCategory,
                                    @Param("toolLoanFee") Integer toolLoanFee);

    // Reports with Date Range

    // 1. Active Loans in Date Range (Created within range AND active)
    @Query("SELECT l FROM Loan l WHERE l.loanStatus = true AND l.deliveryDate BETWEEN :startDate AND :endDate")
    List<Loan> findActiveLoansByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 2. Overdue Loans in Date Range (Return date < current date AND active AND return date in range)
    // Note: "Overdue in range" usually means the return date (when it became overdue) falls in the range.
    @Query("SELECT l FROM Loan l WHERE l.loanStatus = true AND l.returnDate < CURRENT_DATE AND l.returnDate BETWEEN :startDate AND :endDate")
    List<Loan> findOverdueLoansByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 3. Clients with Overdue Loans in Date Range
    @Query("SELECT DISTINCT l.client FROM Loan l WHERE l.loanStatus = true AND l.returnDate < CURRENT_DATE AND l.returnDate BETWEEN :startDate AND :endDate")
    List<Client> findClientsWithOverdueLoansByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 4. Tool Ranking in Date Range
    @Query("SELECT new com.example.tingesoM.Dtos.ToolRankingDto( " +
            "l.tool.name, l.tool.category, l.tool.loanFee, COUNT(l)) " +
            "FROM Loan l " +
            "WHERE l.deliveryDate BETWEEN :startDate AND :endDate " +
            "GROUP BY l.tool.name, l.tool.category, l.tool.loanFee " +
            "ORDER BY COUNT(l) DESC")
    List<ToolRankingDto> findToolRankingByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
