package com.example.tingesoM.Controller;

import com.example.tingesoM.Dtos.CreateToolDto;
import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Dtos.ToolStatusDto;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Service.ServiceImpl.CardexServiceImpl;
import com.example.tingesoM.Service.ServiceImpl.ToolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tool")
public class ToolController {

    @Autowired
    private ToolServiceImpl toolService;
    @Autowired
    private CardexServiceImpl cardexServiceImpl;

    @PostMapping("/createTool")
    public ResponseEntity<Tool> createTool(@RequestBody CreateToolDto tool) {
        Tool toolS=toolService.save(tool);
        cardexServiceImpl.saveCardexTool(tool,toolS);
        return ResponseEntity.ok(toolS);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Tool>> getAllTools() {
        List<Tool> tools = toolService.findAll();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/getAllAvalible")
    public ResponseEntity<List<Tool>> getAllAvalibleTools() {
        List<Tool> tools = toolService.findAllAvalible();
        return ResponseEntity.ok(tools);

    }

    @GetMapping("/getAllNotDelete")
    public ResponseEntity<List<Tool>> getAllNotDeleteTools() {
        List<Tool> tools = toolService.findAllNotDelete();
        return ResponseEntity.ok(tools);

    }

    /*----------------- Change Status --------------------*/

    @PutMapping("/updateStatus")
    public ResponseEntity<Tool> updateStatusTool(@RequestBody ToolStatusDto toolDto) {
        toolService.updateStatusTool(toolDto);
        return ResponseEntity.ok(toolService.findById(toolDto.getIdTool()));
    }

    @PutMapping("/underRepair")
    public ResponseEntity<Tool> underRepairTool(@RequestBody ToolStatusDto toolDto) {
        toolService.underRepairTool(toolDto);
        return ResponseEntity.ok(toolService.findById(toolDto.getIdTool()));
    }

    @PutMapping("/deleteTool")
    public ResponseEntity<Tool> deleteTool(@RequestBody ToolStatusDto toolDto) {
        toolService.deleteTool(toolDto);
        return ResponseEntity.ok(toolService.findById(toolDto.getIdTool()));
    }

    /*-------------------------------------------------------*/

    @GetMapping("/conditions")
    public List<String> getInitialConditions() {
        return toolService.getConditions();
    }

    @PutMapping("/update")
    public ResponseEntity<Tool> updateTool(@RequestBody CreateToolDto toolDto) {
        Tool tool=toolService.updateTool(toolDto);
        return ResponseEntity.ok(tool);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ToolRankingDto>> rankingTools(){
        return ResponseEntity.ok(toolService.findAllToolLoanRanking());
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Tool>> filterTool(@RequestBody ToolRankingDto toolDto){
        List<Tool> tools = toolService.filterTools(toolDto);
        return ResponseEntity.ok(tools);
    }

    /*--------------------------*/
}
