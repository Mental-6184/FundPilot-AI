package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金净值实体
 */
@Data
@TableName("fund_nav")
public class FundNav implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 基金代码 */
    private String fundCode;

    /** 净值日期 */
    private LocalDate navDate;

    /** 单位净值 */
    private BigDecimal unitNav;

    /** 累计净值 */
    private BigDecimal accNav;

    /** 复权净值（用于精确收益率计算） */
    private BigDecimal totalNav;

    /** 日收益率(%) */
    private BigDecimal dailyReturn;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
