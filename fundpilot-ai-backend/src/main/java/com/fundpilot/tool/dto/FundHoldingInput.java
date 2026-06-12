package com.fundpilot.tool.dto;

import lombok.Data;

/**
 * FundHoldingTool - 查询持仓入参
 */
@Data
public class FundHoldingInput {

    /** 基金代码 */
    private String fundCode;

    /** 返回持仓数量，默认10 */
    private int limit = 10;
}
