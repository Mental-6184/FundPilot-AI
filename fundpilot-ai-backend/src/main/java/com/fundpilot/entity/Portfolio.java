package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户投资组合实体
 */
@Data
@TableName("portfolio")
public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 组合名称 */
    private String portfolioName;

    /** 组合描述 / 投资目标 */
    private String description;

    /** 组合整体风险等级 */
    private String riskLevel;

    /** 组合总投入金额（元） */
    private BigDecimal totalAmount;

    /** 状态: 0-归档 1-正常 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
