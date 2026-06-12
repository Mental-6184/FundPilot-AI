package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 重仓股重合度 VO
 */
@Data
public class HoldingOverlapVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 股票代码 */
    private String stockCode;

    /** 股票名称 */
    private String stockName;

    /** 出现在几只基金的重仓中 */
    private Integer appearCount;

    /** 涉及的基金代码列表 */
    private List<String> fundCodes;

    /** 合计持仓占比(%) */
    private BigDecimal totalRatio;
}
