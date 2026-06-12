package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 持仓信息 VO
 */
@Data
public class HoldingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 持仓排名 */
    private Integer rank;

    /** 股票代码 */
    private String stockCode;

    /** 股票名称 */
    private String stockName;

    /** 持仓占净值比(%) */
    private BigDecimal holdRatio;

    /** 持仓市值（万元） */
    private BigDecimal holdAmount;

    /** 持股数量（万股） */
    private BigDecimal holdShares;
}
