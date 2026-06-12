package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金基本信息实体
 */
@Data
@TableName("fund")
public class Fund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 基金代码 */
    private String fundCode;

    /** 基金名称 */
    private String fundName;

    /** 基金类型: EQUITY/BOND/HYBRID/MONEY/INDEX/QDII/FOF/ETF */
    private String fundType;

    /** 风险等级: LOW/MEDIUM_LOW/MEDIUM/MEDIUM_HIGH/HIGH */
    private String riskLevel;

    /** 成立日期 */
    private LocalDate establishDate;

    /** 基金经理ID */
    private Long managerId;

    /** 托管银行 */
    private String custodian;

    /** 业绩比较基准 */
    private String benchmark;

    /** 投资策略说明 */
    private String investStrategy;

    /** 最新规模（亿元） */
    private BigDecimal scale;

    /** 最新份额（亿份） */
    private BigDecimal share;

    /** 管理费率(%) */
    private BigDecimal feeRate;

    /** 申购状态: 0-暂停 1-开放 */
    private Integer buyStatus;

    /** 赎回状态: 0-暂停 1-开放 */
    private Integer redeemStatus;

    /** 状态: 0-下架 1-正常 2-募集期 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
