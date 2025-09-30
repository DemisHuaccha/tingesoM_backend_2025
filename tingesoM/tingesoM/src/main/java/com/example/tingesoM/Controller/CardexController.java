package com.example.tingesoM.Controller;



import com.example.tingesoM.Dtos.CardexDto;
import com.example.tingesoM.Dtos.DtoTime;
import com.example.tingesoM.Entities.Cardex;
import com.example.tingesoM.Entities.Tool;
import com.example.tingesoM.Service.ServiceImpl.CardexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://Localhost:3000")
@RequestMapping("/api/cardex")
public class CardexController {

    @Autowired
    private CardexServiceImpl cardexService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Cardex>> getAllCardexs() {
        List<Cardex> cardexs = cardexService.findAll();
        return ResponseEntity.ok(cardexs);
    }

    @PostMapping("/getForTime")
    public ResponseEntity<List<CardexDto>> getForTime(@RequestBody DtoTime times){
        List<CardexDto> cardexList= cardexService.findForRangeDate(times.getStart(),times.getEnd());
        return ResponseEntity.ok(cardexList);
    }

    @GetMapping("/getCardexTool/{toolId}")
    public ResponseEntity<List<CardexDto>> getCardexTool(@PathVariable Long toolId){
        List<CardexDto> cardexList= cardexService.findCardexTool(toolId);
        return ResponseEntity.ok(cardexList);
    }

    @GetMapping("/getAllCardex")
    public ResponseEntity<List<CardexDto>> getAll(){
        List<CardexDto> cardexDtoList= cardexService.findAllDto();
        return ResponseEntity.ok(cardexDtoList);
    }
}
