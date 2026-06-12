package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 基金列表 VO（搜索结果）
 */
@Data
public class FundListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    private String fundCode;

    /** 基金名称 */
    private String fundName;

    /** 基金类型 */
    private String fundType;

    /** 基金类型中文 */
    private String fundTypeName;

    /** 风险等级 */
    private String riskLevel;

    /** 风险等级中文 */
    private String riskLevelName;

    /** 成立日期 */
    private LocalDate establishDate;

    /** 最新规模（亿元） */
    private BigDecimal scale;

    /** 最新单位净值 */
    private BigDecimal latestNav;

    /** 最新净值日期 */
    private LocalDate latestNavDate;

    /** 日收益率(%) */
    private BigDecimal dailyReturn;

    /** 近1月收益率(%) */
    private BigDecimal monthlyReturn;

    /** 近1年收益率(%) */
    private BigDecimal yearlyReturn;
}
