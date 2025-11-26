package com.example.tingesoM.Repositorie;

import com.example.tingesoM.Dtos.LoanResponseDto;
import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    @Query("SELECT l FROM Loan l WHERE l.loanStatus = true AND l.penalty = false")
    List<Loan> findActiveAndOnTimeLoans();

    //Return loan actives and delayed
    @Query("SELECT l FROM Loan l WHERE l.loanStatus = true AND l.penalty = true")
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


    @Query("SELECT COUNT(l) >= 1 FROM Loan l " +
            "WHERE l.client.idCustomer = :clientId " +
            "AND l.loanStatus = true " +
            "AND l.tool.name = :toolName " +
            "AND l.tool.category = :toolCategory " +
            "AND l.tool.loanFee = :toolLoanFee")
    boolean clientHasNoMatchingLoan(@Param("clientId") Long clientId,
                                    @Param("toolName") String toolName,
                                    @Param("toolCategory") String toolCategory,
                                    @Param("toolLoanFee") Integer toolLoanFee);

}
