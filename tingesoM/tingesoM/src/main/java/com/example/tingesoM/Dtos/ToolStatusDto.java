package com.example.tingesoM.Dtos;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ToolStatusDto {


    private Long idTool;

    private Boolean status;

    private Boolean underRepair;

    private Boolean deleteStatus;

    /*--------------------------*/

    private String email;
}
