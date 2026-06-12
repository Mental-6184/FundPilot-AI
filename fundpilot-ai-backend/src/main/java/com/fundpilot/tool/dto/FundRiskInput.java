package com.fundpilot.tool.dto;

import lombok.Data;

/**
 * FundRiskTool - 查询风险指标入参
 */
@Data
public class FundRiskInput {

    /** 基金代码 */
    private String fundCode;

    /** 无风险利率（%），默认2.0 */
    private String riskFreeRate = "2.0000";

    /** 基准指数代码（用于计算Alpha/Beta），可选 */
    private String benchmarkCode;
}
