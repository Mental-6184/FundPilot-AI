package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 组合明细实体（组合内每只基金）
 */
@Data
@TableName("portfolio_item")
public class PortfolioItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属组合ID */
    private Long portfolioId;

    /** 基金代码 */
    private String fundCode;

    /** 投入金额（元） */
    private BigDecimal investAmount;

    /** 持有份额 */
    private BigDecimal investShares;

    /** 目标配置比例(%) */
    private BigDecimal targetRatio;

    /** 实际持仓比例(%) */
    private BigDecimal actualRatio;

    /** 买入均价（单位净值） */
    private BigDecimal buyPrice;

    /** 首次买入日期 */
    private LocalDate buyDate;

    /** 备注 */
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
