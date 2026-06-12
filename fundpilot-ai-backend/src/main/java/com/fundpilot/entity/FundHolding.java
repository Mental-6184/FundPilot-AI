package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金持仓实体
 */
@Data
@TableName("fund_holding")
public class FundHolding implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 基金代码 */
    private String fundCode;

    /** 报告日期（季报截止日） */
    private LocalDate reportDate;

    /** 持仓股票代码 */
    private String stockCode;

    /** 持仓股票名称 */
    private String stockName;

    /** 持仓占净值比(%) */
    private BigDecimal holdRatio;

    /** 持仓市值（万元） */
    private BigDecimal holdAmount;

    /** 持股数量（万股） */
    private BigDecimal holdShares;

    /** 持仓排名 */
    private Integer rank;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
