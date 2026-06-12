package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 组合分析结果 VO
 */
@Data
public class PortfolioAnalysisVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 组合ID */
    private Long portfolioId;

    /** 组合名称 */
    private String portfolioName;

    // ==================== 收益指标 ====================

    /** 组合累计收益率(%) */
    private BigDecimal cumulativeReturn;

    /** 组合年化收益率(%) */
    private BigDecimal annualizedReturn;

    /** 组合总盈亏（元） */
    private BigDecimal totalProfit;

    // ==================== 风险指标 ====================

    /** 组合年化波动率(%) */
    private BigDecimal volatility;

    /** 组合最大回撤(%) */
    private BigDecimal maxDrawdown;

    /** 夏普比率 */
    private BigDecimal sharpeRatio;

    // ==================== 风险评分 ====================

    /** 综合风险评分 (1-100) */
    private Integer riskScore;

    /** 风险等级: LOW/MEDIUM_LOW/MEDIUM/MEDIUM_HIGH/HIGH */
    private String riskLevel;

    /** 风险等级中文 */
    private String riskLevelName;

    /** 风险评分详情 */
    private RiskScoreDetailVO riskScoreDetail;

    // ==================== 行业分布 ====================

    /** 行业分布列表 */
    private List<IndustryDistributionVO> industryDistribution;

    // ==================== 重仓股重合度 ====================

    /** 基金间重仓股重合分析 */
    private List<HoldingOverlapVO> holdingOverlaps;

    /** 重合度评分 (0-100，越高表示持仓越集中) */
    private Integer overlapScore;

    // ==================== 持仓明细 ====================

    /** 组合内基金列表 */
    private List<PortfolioFundItemVO> fundItems;
}
