package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 基金净值数据 VO（接口 /fund/{code}/nav 返回）
 */
@Data
public class FundNavVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    private String fundCode;

    /** 最新单位净值 */
    private BigDecimal latestNav;

    /** 最新累计净值 */
    private BigDecimal latestAccNav;

    /** 最新净值日期 */
    private LocalDate latestNavDate;

    /** 日收益率(%) */
    private BigDecimal dailyReturn;

    /** 净值走势列表 */
    private List<NavPointVO> trend;
}
