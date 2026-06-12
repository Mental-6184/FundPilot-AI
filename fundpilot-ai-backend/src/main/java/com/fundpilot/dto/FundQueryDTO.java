package com.fundpilot.dto;

import com.fundpilot.common.result.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基金搜索条件 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FundQueryDTO extends PageQuery {

    /** 关键词（基金代码或名称，模糊搜索） */
    private String keyword;

    /** 基金类型: EQUITY/BOND/HYBRID/MONEY/INDEX/QDII/FOF/ETF */
    private String fundType;

    /** 风险等级: LOW/MEDIUM_LOW/MEDIUM/MEDIUM_HIGH/HIGH */
    private String riskLevel;

    /** 排序字段: dailyReturn/scale/establishDate */
    private String sortBy;

    /** 排序方向: asc/desc */
    private String sortOrder;
}
