package com.example.tingesoM.Service.ServiceImpl;

import com.example.tingesoM.Dtos.*;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Entities.User;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import com.example.tingesoM.Service.Interface.ToolService;
import com.example.tingesoM.Service.ServiceImpl.Users.ClientServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToolServiceImpl implements ToolService {
    @Autowired
    ToolRepositorie toolRepositorie;

    @Autowired
    LoanRepositorie loanRepositorie;

    @Autowired
    UserRepositorie userRepositorie;

    @Autowired
    CardexServiceImpl cardexService;
    @Autowired
    ClientServiceImpl clientService;

    //Create
    @Override
    public Tool save(CreateToolDto toolT) {

        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(toolT.getEmail());
        if(!resultado.isPresent()){
            throw new IllegalArgumentException("User with email " + toolT.getEmail() + " not found");
        }

        for(int i=1; i<toolT.getQuantity();i++) {
            Tool tool = new Tool();
            tool.setStatus(Boolean.TRUE);
            tool.setDeleteStatus(Boolean.FALSE);
            tool.setUnderRepair(Boolean.FALSE);
            /*-------------------------------*/
            tool.setName(toolT.getName());
            tool.setCategory(toolT.getCategory());
            tool.setInitialCondition(toolT.getInitialCondition());
            tool.setLoanFee(toolT.getLoanFee());
            tool.setPenaltyForDelay(toolT.getPenaltyForDelay());
            tool.setReplacementValue(toolT.getReplacementValue());
            tool.setDescription(toolT.getDescription());
            tool.setDamageValue(toolT.getDamageValue());
            toolRepositorie.save(tool);
        }
        /*--------------------------------*/

        Tool tool = new Tool();
        tool.setStatus(Boolean.TRUE);
        tool.setDeleteStatus(Boolean.FALSE);
        tool.setUnderRepair(Boolean.FALSE);
        /*-------------------------------*/
        tool.setName(toolT.getName());
        tool.setCategory(toolT.getCategory());
        tool.setInitialCondition(toolT.getInitialCondition());
        tool.setLoanFee(toolT.getLoanFee());
        tool.setPenaltyForDelay(toolT.getPenaltyForDelay());
        tool.setReplacementValue(toolT.getReplacementValue());
        tool.setDescription(toolT.getDescription());
        tool.setDamageValue(toolT.getDamageValue());
        toolRepositorie.save(tool);
        return tool;
    }

    //Read
    @Override
    public Tool findById(Long id){
        return toolRepositorie.findById(id).orElse(null);
    }
    @Override
    public List<Tool> findAll() {
        clientService.restrictClientsWithDelayedLoans();
        return toolRepositorie.findAll();
    }

    @Override
    public List<Tool> findAllAvalible() {
        return toolRepositorie.findAll().stream().filter(t -> t.getStatus().equals(Boolean.TRUE)).collect(Collectors.toList());
    }

    @Override
    public List<Tool> findAllNotDelete() {
        return toolRepositorie.findAll().stream().filter(t -> t.getDeleteStatus().equals(Boolean.FALSE)).collect(Collectors.toList());
    }

    @Override
    public List<String> getConditions(){
        return Arrays.stream(InitialCondition.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupToolsDto> groupTools(){
        return toolRepositorie.agroupByNameCategoryAndLoanFee();
    }

    //Update
    @Override
    public Tool updateTool(CreateToolDto toolDto){
        Tool oldTool = toolRepositorie.findById(toolDto.getIdTool()).orElse(null);
        if(oldTool!=null){
            oldTool.setName(toolDto.getName());
            oldTool.setCategory(toolDto.getCategory());
            oldTool.setInitialCondition(toolDto.getInitialCondition());
            oldTool.setLoanFee(toolDto.getLoanFee());
            oldTool.setPenaltyForDelay(toolDto.getPenaltyForDelay());
            oldTool.setReplacementValue(toolDto.getReplacementValue());
            toolRepositorie.save(oldTool);
        }else {
            throw new EntityNotFoundException("Tool con ID " + toolDto.getIdTool() + " no encontrada");
        }
        cardexService.saveCardexUpdateTool(toolDto, oldTool);
        return oldTool;
    }

    //Delete
    @Override
    public void updateStatusTool(ToolStatusDto toolDto){
        Tool tool;
        tool = toolRepositorie.findById(toolDto.getIdTool()).orElse(null);
        if(tool!=null){
            if(toolDto.getStatus().equals(Boolean.TRUE)){
                tool.setStatus(Boolean.FALSE);
            }
            else {
                tool.setStatus(Boolean.TRUE);
            }
        }
        else{
            throw new EntityNotFoundException("Tool with ID " + toolDto.getIdTool() + " not found");
        }
        toolRepositorie.save(tool);

        cardexService.saveCardexUpdateStatusTool( toolDto, tool);
    }


    @Override
    public void underRepairTool(ToolStatusDto toolDto){
        Tool tool;
        tool = toolRepositorie.findById(toolDto.getIdTool()).orElse(null);
        if(tool!=null){
            if(toolDto.getUnderRepair().equals(Boolean.FALSE)) {
                tool.setUnderRepair(Boolean.TRUE);
            }
            else {
                tool.setUnderRepair(Boolean.FALSE);
            }
        }
        else{
            throw new EntityNotFoundException("Tool with ID " + toolDto.getIdTool() + " not found");
        }
        toolRepositorie.save(tool);
        cardexService.saveCardexRepairTool( toolDto, tool);
    }

    //Delete
    @Override
    public void deleteTool(ToolStatusDto toolDto){
        Tool tool;
        tool = toolRepositorie.findById(toolDto.getIdTool()).orElse(null);
        if(tool!=null){
            if(toolDto.getDeleteStatus().equals(Boolean.TRUE)){
                tool.setDeleteStatus(Boolean.FALSE);
            }
            else {
                tool.setDeleteStatus(Boolean.TRUE);
            }
        }
        else{
            throw new EntityNotFoundException("Tool with ID " + toolDto.getIdTool() + " not found");
        }
        toolRepositorie.save(tool);
        cardexService.saveCardexDeleteTool( toolDto, tool);
    }

    /*-----------------------------*/

    //Tool loan ranking

    @Override
    public List<ToolRankingDto> findAllToolLoanRanking(){
        return loanRepositorie.findMostLoanedToolsWithDetails();
    }

    @Override
    public List<String> debugRanking() {
        return loanRepositorie.debugRanking();
    }

    @Override
    public List<Tool> filterTools(ToolRankingDto toolDto){
        return toolRepositorie.findByNameCategoryAndLoanFee(toolDto.getNameTool(),toolDto.getCategoryTool(),toolDto.getFeeTool());
    }

    @Override
    public List<Tool> searchToolsById(String idPrefix) {
        return toolRepositorie.findAvailableByIdStartingWith(idPrefix);
    }

}
