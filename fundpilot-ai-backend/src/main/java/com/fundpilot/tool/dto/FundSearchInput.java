package com.fundpilot.tool.dto;

import lombok.Data;

/**
 * FundInfoTool - 搜索基金入参
 */
@Data
public class FundSearchInput {

    /** 搜索关键词（基金代码或名称） */
    private String keyword;

    /** 基金类型: EQUITY/BOND/HYBRID/MONEY/INDEX/QDII */
    private String fundType;

    /** 风险等级: LOW/MEDIUM_LOW/MEDIUM/MEDIUM_HIGH/HIGH */
    private String riskLevel;

    /** 返回条数，默认20 */
    private int limit = 20;
}
