package com.fundpilot.tool.dto;

import lombok.Data;

/**
 * FundReturnTool - 查询收益率入参
 */
@Data
public class FundReturnInput {

    /** 基金代码 */
    private String fundCode;

    /** 分析周期: 1w/1m/3m/6m/1y/3y/all，默认all */
    private String period = "all";

    /** 无风险利率（%），默认2.0 */
    private String riskFreeRate = "2.0000";
}
