package com.fundpilot.tool.dto;

import lombok.Data;

/**
 * FundRecommendTool - 基金推荐入参
 */
@Data
public class FundRecommendInput {

    /** 偏好基金类型: EQUITY/BOND/HYBRID/MONEY/INDEX/QDII */
    private String fundType;

    /** 偏好风险等级: LOW/MEDIUM_LOW/MEDIUM/MEDIUM_HIGH/HIGH */
    private String riskLevel;

    /** 排序偏好: RETURN(收益优先)/RISK(低风险优先)/BALANCED(均衡) */
    private String preference = "BALANCED";

    /** 返回条数，默认5 */
    private int limit = 5;
}
