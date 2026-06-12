package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 基金详情 VO
 */
@Data
public class FundDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    private String fundCode;

    /** 基金名称 */
    private String fundName;

    /** 基金类型 */
    private String fundType;

    /** 风险等级 */
    private String riskLevel;

    /** 成立日期 */
    private LocalDate establishDate;

    /** 业绩基准 */
    private String benchmark;

    /** 投资策略 */
    private String investStrategy;

    /** 最新规模（亿元） */
    private BigDecimal scale;

    /** 最新份额（亿份） */
    private BigDecimal share;

    /** 管理费率(%) */
    private BigDecimal feeRate;

    /** 托管银行 */
    private String custodian;

    /** 申购状态 */
    private Integer buyStatus;

    /** 赎回状态 */
    private Integer redeemStatus;

    /** 最新单位净值 */
    private BigDecimal latestNav;

    /** 最新累计净值 */
    private BigDecimal latestAccNav;

    /** 最新净值日期 */
    private LocalDate latestNavDate;

    /** 基金经理 */
    private ManagerVO manager;
}
