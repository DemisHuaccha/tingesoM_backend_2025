package com.example.tingesoM.Service.ServiceImpl;

import com.example.tingesoM.Dtos.*;
import com.example.tingesoM.Entities.*;
import com.example.tingesoM.Repositorie.CardexRepositorie;
import com.example.tingesoM.Repositorie.LoanRepositorie;
import com.example.tingesoM.Repositorie.ToolRepositorie;
import com.example.tingesoM.Repositorie.UserRepositorie.UserRepositorie;
import com.example.tingesoM.Service.Interface.CardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CardexServiceImpl implements CardexService {
    @Autowired
    private CardexRepositorie cardexRepositorie;
    @Autowired
    private UserRepositorie userRepositorie;
    @Autowired
    private ToolRepositorie toolRepositorie;
    @Autowired
    private LoanRepositorie loanRepositorie;

    //Create

    public void save(Cardex cardex) {
        cardexRepositorie.save(cardex);
    }

    /*Create cardex for loan*/

    //Register loan
    public void saveCardexLoan(Long toolId, String email, Loan loan) {
        Cardex cardex = new Cardex();
        Optional<Tool> tool= toolRepositorie.findById(toolId);

        //Verificacion de existencia de tool
        if(tool.isPresent()){
            cardex.setTool(tool.get());
        }
        else {
            throw new IllegalArgumentException("Tool with ID " + toolId + " not found");
        }

        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(email);
        if(resultado.isPresent()){
           cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with Email " + email + " not found");
        }

        //Creation of cardex
        cardex.setTypeMove("Create Loan");
        cardex.setMoveDate(LocalDate.now());
        cardex.setLoan(loan);
        cardex.setAmount(null);
        cardex.setDescription("User with email "+email+" register a loan where rut client is: "+ loan.getClient().getRut());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }

    /*Create cardex for tool*/

    public void saveCardexTool(CreateToolDto toolT, Tool tool) {
        Cardex cardex = new Cardex();
        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(toolT.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + toolT.getEmail() + " not found");
        }
        cardex.setTool(tool);
        cardex.setLoan(null);
        cardex.setTypeMove("Create Tool");
        cardex.setAmount(null);
        cardex.setDescription("User with email "+toolT.getEmail() +" register a tool with name: "+tool.getName()+", category: "+tool.getCategory()+", and price: "+tool.getLoanFee());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(toolT.getQuantity());

        cardexRepositorie.save(cardex);
    }

    /*Create cardex for client*/

    public void saveCardexClient(CreateClientDto clientDto, Client client) {
        Cardex cardex = new Cardex();
        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(clientDto.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + clientDto.getEmail() + " not found");
        }
        cardex.setClient(client);
        cardex.setLoan(null);
        cardex.setTool(null);
        cardex.setTypeMove("Create Client");
        cardex.setAmount(null);
        cardex.setDescription("User with email "+clientDto.getEmail() +" register a client with rut: "+client.getRut());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }

    /*Create cardex for update tool*/

    @Override
    public void saveCardexUpdateTool(CreateToolDto toolDto, Tool tool) {
        Cardex cardex = new Cardex();
        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(toolDto.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + toolDto.getEmail() + " not found");
        }
        cardex.setClient(null);
        cardex.setLoan(null);
        cardex.setTool(tool);
        cardex.setTypeMove("Update Tool");
        cardex.setAmount(null);
        cardex.setDescription("User with email "+toolDto.getEmail() +" update a tool with id: "+tool.getIdTool());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }

    @Override
    public void saveCardexUpdateStatusTool(ToolStatusDto toolDto, Tool tool) {
        Cardex cardex = new Cardex();
        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(toolDto.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + toolDto.getEmail() + " not found");
        }
        cardex.setClient(null);
        cardex.setLoan(null);
        cardex.setTool(tool);
        cardex.setTypeMove("Tool update status change to "+tool.getUnderRepair());
        cardex.setAmount(null);
        cardex.setDescription("User with email "+toolDto.getEmail() +" change status tool, of tool with id: "+tool.getIdTool());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }

    @Override
    public void saveCardexRepairTool(ToolStatusDto toolDto, Tool tool) {
        Cardex cardex = new Cardex();
        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(toolDto.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + toolDto.getEmail() + " not found");
        }
        cardex.setClient(null);
        cardex.setLoan(null);
        cardex.setTool(tool);
        cardex.setTypeMove("Tool Repair status change to "+tool.getUnderRepair());
        cardex.setAmount(null);
        cardex.setDescription("User with email "+toolDto.getEmail() +" update repair status of tool with id: "+tool.getIdTool());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }

    @Override
    public void saveCardexDeleteTool(ToolStatusDto toolDto, Tool tool) {
        Cardex cardex = new Cardex();
        //Verificacion de existencia de user
        Optional<User> resultado = userRepositorie.findByEmail(toolDto.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + toolDto.getEmail() + " not found");
        }
        cardex.setClient(null);
        cardex.setLoan(null);
        cardex.setTool(tool);
        cardex.setTypeMove("Tool Delete");
        cardex.setAmount(null);
        cardex.setDescription("User with email "+toolDto.getEmail() +" deleta tool with id: "+tool.getIdTool());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }

    @Override
    public void saveCardexReturnLoan(ReturnLoanDto loanDto) {
        Cardex cardex = new Cardex();
        Optional<User> resultado = userRepositorie.findByEmail(loanDto.getEmail());
        if(resultado.isPresent()){
            cardex.setUser(resultado.get());
        }
        else {
            throw new IllegalArgumentException("User with email " + loanDto.getEmail() + " not found");
        }
        cardex.setClient(null);
        cardex.setLoan(loanRepositorie.findById(loanDto.getLoanId()).orElse(null));
        cardex.setTool(null);
        cardex.setTypeMove("Loan Finished");
        cardex.setAmount(null);
        cardex.setDescription("User with email "+loanDto.getEmail() +" return loan with id: "+ loanDto.getLoanId());
        cardex.setMoveDate(LocalDate.now());
        cardex.setQuantity(1);

        cardexRepositorie.save(cardex);
    }


    //Read
    @Override
    public Cardex findById(Long id){
        return cardexRepositorie.findById(id).orElse(null);
    }
    @Override
    public List<Cardex> findAll() {
        return cardexRepositorie.findAll();
    }

    @Override
    public  List<CardexDto> findAllDto(){
        return cardexRepositorie.findAllCardex();
    }

    /*Find for range date*/
    @Override
    public List<CardexDto> findForRangeDate(LocalDate start, LocalDate end) {
        if(start==null && end==null){
            return cardexRepositorie.findAllCardex();
        }
        else if(end == null){
            return cardexRepositorie.findMovementsFromDate(start);
        }
        else if(start == null){
            return cardexRepositorie.findMovementsUntilDate(end);
        }
        return cardexRepositorie.findCardexDateRange(start, end);

    }

    /*Find cardex tool*/
    @Override
    public List<CardexDto> findCardexTool(Long toolId){
        return cardexRepositorie.findCardexByToolId(toolId);
    }

    //Update

    //Delete
}
