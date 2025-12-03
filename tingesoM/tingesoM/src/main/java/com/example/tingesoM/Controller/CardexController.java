package com.example.tingesoM.Controller;



import com.example.tingesoM.Dtos.CardexDto;
import com.example.tingesoM.Dtos.DtoTime;
import com.example.tingesoM.Entities.Cardex;
import com.example.tingesoM.Service.ServiceImpl.CardexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
    @PostMapping("/getForTime")
    public ResponseEntity<List<CardexDto>> getForTime(@RequestBody DtoTime times){
        List<CardexDto> cardexList= cardexService.findForRangeDate(times.getStart(),times.getEnd(), times.getIdTool());
        return ResponseEntity.ok(cardexList);
    }

    @PostMapping("/getCardexTool")
    public ResponseEntity<List<CardexDto>> getCardexTool(@RequestBody CardexDto cardexDto){
        List<CardexDto> cardexList= cardexService.findCardexTool(cardexDto.getToolId());
        return ResponseEntity.ok(cardexList);
    }

    @GetMapping("/getAllCardex")
    public ResponseEntity<List<CardexDto>> getAll(){
        List<CardexDto> cardexDtoList= cardexService.findAllDto();
        return ResponseEntity.ok(cardexDtoList);
    }
}
