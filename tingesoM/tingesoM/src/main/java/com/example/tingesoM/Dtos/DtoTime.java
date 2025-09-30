package com.example.tingesoM.Dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DtoTime {
    private LocalDate start;
    private LocalDate end;
}
