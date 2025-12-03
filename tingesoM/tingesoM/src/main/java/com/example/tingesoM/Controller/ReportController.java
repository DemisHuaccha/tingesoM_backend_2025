package com.example.tingesoM.Controller;

import com.example.tingesoM.Dtos.ToolRankingDto;
import com.example.tingesoM.Entities.Client;
import com.example.tingesoM.Entities.Loan;
import com.example.tingesoM.Service.Interface.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:8070", "http://localhost:5173", "http://localhost", "http://127.0.0.1"})
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/active-loans")
    public ResponseEntity<List<Loan>> getActiveLoans(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getActiveLoans(startDate, endDate));
    }

    @GetMapping("/overdue-loans")
    public ResponseEntity<List<Loan>> getOverdueLoans(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getOverdueLoans(startDate, endDate));
    }

    @GetMapping("/overdue-clients")
    public ResponseEntity<List<Client>> getClientsWithOverdueLoans(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getClientsWithOverdueLoans(startDate, endDate));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ToolRankingDto>> getToolRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getToolRanking(startDate, endDate));
    }
}
