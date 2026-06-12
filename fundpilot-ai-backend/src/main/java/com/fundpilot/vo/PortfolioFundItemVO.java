package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 组合内单只基金 VO
 */
@Data
public class PortfolioFundItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    private String fundCode;

    /** 基金名称 */
    private String fundName;

    /** 基金类型 */
    private String fundType;

    /** 风险等级 */
    private String riskLevel;

    /** 投入金额（元） */
    private BigDecimal investAmount;

    /** 持有份额 */
    private BigDecimal investShares;

    /** 目标配置比例(%) */
    private BigDecimal targetRatio;

    /** 实际配置比例(%) */
    private BigDecimal actualRatio;

    /** 买入均价 */
    private BigDecimal buyPrice;

    /** 最新净值 */
    private BigDecimal latestNav;

    /** 持仓盈亏（元） */
    private BigDecimal profit;

    /** 持仓收益率(%) */
    private BigDecimal returnRate;

    /** 首次买入日期 */
    private LocalDate buyDate;
}
