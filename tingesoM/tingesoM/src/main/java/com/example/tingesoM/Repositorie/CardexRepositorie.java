package com.example.tingesoM.Repositorie;

import com.example.tingesoM.Dtos.CardexDto;
import com.example.tingesoM.Entities.Cardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardexRepositorie extends JpaRepository<Cardex,Long> {

    //Find for range time
    @Query("""
    SELECT new com.example.tingesoM.Dtos.CardexDto(
        c.id, c.moveDate, c.typeMove, c.description, c.amount, c.quantity,c.user.email,c.tool.idTool,c.loan.loanId,c.client.rut)
    FROM Cardex c
    LEFT JOIN c.tool t
    LEFT JOIN c.loan l
    LEFT JOIN c.client cl
    JOIN c.user u 
    WHERE c.moveDate
    BETWEEN :startDate AND :endDate ORDER BY c.moveDate ASC
                 """)
    List<CardexDto> findCardexDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
    SELECT new com.example.tingesoM.Dtos.CardexDto(
        c.id, c.moveDate, c.typeMove, c.description, c.amount, c.quantity,c.user.email,c.tool.idTool,c.loan.loanId,c.client.rut) 
    FROM Cardex c
    LEFT JOIN c.tool t
    LEFT JOIN c.loan l
    LEFT JOIN c.client cl
    JOIN c.user u 
    WHERE c.moveDate >= :startDate 
    ORDER BY c.moveDate ASC
            """)
    List<CardexDto> findMovementsFromDate(@Param("startDate") LocalDate startDate);

    @Query(""" 
    SELECT new com.example.tingesoM.Dtos.CardexDto(
        c.id, c.moveDate, c.typeMove, c.description, c.amount, c.quantity,c.user.email,c.tool.idTool,c.loan.loanId,c.client.rut)
    FROM Cardex c
    LEFT JOIN c.tool t
    LEFT JOIN c.loan l
    LEFT JOIN c.client cl
    JOIN c.user u
    WHERE c.moveDate <= :endDate 
    ORDER BY c.moveDate ASC
               """)
    List<CardexDto> findMovementsUntilDate(@Param("endDate") LocalDate endDate);




    //Find cardex of tool by id
    @Query("""
    SELECT new com.example.tingesoM.Dtos.CardexDto(
        c.id, c.moveDate, c.typeMove, c.description, c.amount, c.quantity,c.user.email,c.tool.idTool,c.loan.loanId,c.client.rut)
    FROM Cardex c 
    LEFT JOIN c.tool t
    LEFT JOIN c.loan l
    LEFT JOIN c.client cl
    JOIN c.user u
    WHERE c.tool.idTool = :toolId ORDER BY c.moveDate DESC
        """)
    List<CardexDto> findCardexByToolId(@Param("toolId") Long toolId);

    @Query("""
    SELECT new com.example.tingesoM.Dtos.CardexDto(
        c.id, c.moveDate, c.typeMove, c.description, c.amount, c.quantity,c.user.email,c.tool.idTool,c.loan.loanId,c.client.rut)
    FROM Cardex c
    LEFT JOIN c.tool t
    LEFT JOIN c.loan l
    LEFT JOIN c.client cl
    JOIN c.user u
    """)
    List<CardexDto> findAllCardex();


}
