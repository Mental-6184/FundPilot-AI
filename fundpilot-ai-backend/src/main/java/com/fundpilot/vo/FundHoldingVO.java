package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 基金持仓数据 VO（接口 /fund/{code}/holding 返回）
 */
@Data
public class FundHoldingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    private String fundCode;

    /** 最新报告日期 */
    private LocalDate reportDate;

    /** 前十大持仓列表 */
    private List<HoldingVO> topHoldings;
}
