package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 组合详情 VO
 */
@Data
public class PortfolioVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 组合ID */
    private Long id;

    /** 组合名称 */
    private String portfolioName;

    /** 组合描述 */
    private String description;

    /** 组合整体风险等级 */
    private String riskLevel;

    /** 组合总投入金额（元） */
    private BigDecimal totalAmount;

    /** 状态: 0-归档 1-正常 */
    private Integer status;

    /** 基金数量 */
    private Integer fundCount;

    /** 组合内基金明细 */
    private List<PortfolioFundItemVO> funds;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
