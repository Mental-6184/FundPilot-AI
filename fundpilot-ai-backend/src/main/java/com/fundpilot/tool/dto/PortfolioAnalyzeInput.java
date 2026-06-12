package com.fundpilot.tool.dto;

import lombok.Data;

/**
 * PortfolioAnalyzeTool - 组合分析入参
 */
@Data
public class PortfolioAnalyzeInput {

    /** 用户ID */
    private Long userId;

    /** 组合ID */
    private Long portfolioId;
}
