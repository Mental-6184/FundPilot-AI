package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 基金业绩指标 VO
 * 所有指标由 Java 精确计算，非 LLM 编造
 */
@Data
public class FundPerformanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    private String fundCode;

    // ==================== 收益率指标 ====================

    /** 近1周收益率(%) */
    private BigDecimal return1w;

    /** 近1月收益率(%) */
    private BigDecimal return1m;

    /** 近3月收益率(%) */
    private BigDecimal return3m;

    /** 近6月收益率(%) */
    private BigDecimal return6m;

    /** 近1年收益率(%) */
    private BigDecimal return1y;

    /** 近3年收益率(%) */
    private BigDecimal return3y;

    /** 成立以来收益率(%) */
    private BigDecimal returnSinceEstablish;

    /** 年化收益率(%) */
    private BigDecimal annualizedReturn;

    // ==================== 风险指标 ====================

    /** 年化波动率(%) */
    private BigDecimal annualizedVolatility;

    /** 最大回撤(%) */
    private BigDecimal maxDrawdown;

    /** 最大回撤开始索引 */
    private Integer maxDrawdownStart;

    /** 最大回撤结束索引 */
    private Integer maxDrawdownEnd;

    // ==================== 风险调整收益指标 ====================

    /** 夏普比率 */
    private BigDecimal sharpeRatio;

    /** 索提诺比率 */
    private BigDecimal sortinoRatio;

    /** 卡尔马比率 = 年化收益率 / 最大回撤 */
    private BigDecimal calmarRatio;

    // ==================== 相对基准指标 ====================

    /** Beta 系数 */
    private BigDecimal beta;

    /** Alpha（詹森阿尔法，%） */
    private BigDecimal alpha;

    /** 基准代码 */
    private String benchmarkCode;
}
