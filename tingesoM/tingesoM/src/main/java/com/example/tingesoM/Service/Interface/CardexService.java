package com.example.tingesoM.Service.Interface;

import com.example.tingesoM.Dtos.CardexDto;
import com.example.tingesoM.Dtos.CreateToolDto;
import com.example.tingesoM.Dtos.ReturnLoanDto;
import com.example.tingesoM.Dtos.ToolStatusDto;
import com.example.tingesoM.Entities.Cardex;
import com.example.tingesoM.Entities.Tool;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CardexService {

    List<CardexDto> findForRangeDate(LocalDate start, LocalDate end);
    List<CardexDto> findCardexTool(Long toolId);
    List<CardexDto> findAllDto();

    /**/
    void saveCardexUpdateTool(CreateToolDto toolDto, Tool tool);

    void saveCardexUpdateStatusTool(ToolStatusDto toolDto, Tool tool);

    void saveCardexRepairTool(ToolStatusDto toolDto, Tool tool);

    void saveCardexDeleteTool(ToolStatusDto toolDto, Tool tool);

    void saveCardexReturnLoan(ReturnLoanDto loanDto);
}
