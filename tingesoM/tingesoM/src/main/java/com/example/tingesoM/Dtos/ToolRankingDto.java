package com.example.tingesoM.Dtos;

import lombok.Data;

@Data
public class ToolRankingDto {

    private  String	nameTool;
	private String	categoryTool;
	private Integer	feeTool;
	private Long quantityTool;

    public ToolRankingDto(String nameTool, String categoryTool, Integer feeTool, Long quantityTool) {
        this.nameTool = nameTool;
        this.categoryTool = categoryTool;
        this.feeTool = feeTool;
        this.quantityTool = quantityTool;
    }
}
