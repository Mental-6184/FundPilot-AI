package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 净值数据点 VO (用于 ECharts 图表)
 */
@Data
public class NavPointVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日期 */
    private LocalDate date;

    /** 单位净值 */
    private BigDecimal unitNav;

    /** 累计净值 */
    private BigDecimal accNav;
}
