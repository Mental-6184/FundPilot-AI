package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 基金经理实体
 */
@Data
@TableName("fund_manager")
public class FundManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 姓名 */
    private String name;

    /** 性别: 0-女 1-男 */
    private Integer gender;

    /** 所属基金公司 */
    private String company;

    /** 从业年限（年） */
    private BigDecimal tenureYears;

    /** 学历 / 毕业院校 */
    private String education;

    /** 个人简介 */
    private String biography;

    /** 投资风格: 价值/成长/均衡/量化 */
    private String investStyle;

    /** 最佳任期回报(%) */
    private BigDecimal bestReturn;

    /** 当前在管基金数 */
    private Integer manageCount;

    /** 累计管理基金数 */
    private Integer totalManage;

    /** 状态: 0-离任 1-在职 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
