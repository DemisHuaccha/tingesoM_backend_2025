package com.example.tingesoM.Service.Interface;

import com.example.tingesoM.Dtos.CreateToolDto;
import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Dtos.ToolStatusDto;
import com.example.tingesoM.Entities.Tool;

import java.util.List;

public interface ToolService {
    Tool save(CreateToolDto tool);
    Tool findByName(String name);
    Tool findById(Long id);
    List<Tool> findAll();
    List<Tool> findAllAvalible();
    Tool updateTool(CreateToolDto tool);
    void deleteTool(ToolStatusDto toolDto);
    void updateStatusTool(ToolStatusDto toolDto);
    void underRepairTool(ToolStatusDto toolDto);
    List<ToolRankingDto> findAllToolLoanRanking();
    List<Tool> filterTools(ToolRankingDto toolDto);
}
