package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 风险评分详情 VO
 */
@Data
public class RiskScoreDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 波动率得分 (0-100) */
    private Integer volatilityScore;

    /** 最大回撤得分 (0-100) */
    private Integer drawdownScore;

    /** 集中度得分 (0-100) */
    private Integer concentrationScore;

    /** 基金风险等级得分 (0-100) */
    private Integer fundRiskScore;

    /** 波动率权重 */
    private BigDecimal volatilityWeight;

    /** 最大回撤权重 */
    private BigDecimal drawdownWeight;

    /** 集中度权重 */
    private BigDecimal concentrationWeight;

    /** 基金风险等级权重 */
    private BigDecimal fundRiskWeight;
}
