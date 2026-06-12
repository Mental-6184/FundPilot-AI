package com.fundpilot.tool.dto;

import lombok.Data;

import java.util.List;

/**
 * FundCompareTool - 基金对比入参
 */
@Data
public class FundCompareInput {

    /** 要对比的基金代码列表（2-5只） */
    private List<String> fundCodes;

    /** 对比维度: ALL/RETURN/RISK/HOLDING，默认ALL */
    private String dimension = "ALL";
}
