package com.fundpilot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 组合明细 DTO
 */
@Data
public class PortfolioItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    @NotBlank(message = "基金代码不能为空")
    private String fundCode;

    /** 投入金额（元） */
    private BigDecimal investAmount;

    /** 持有份额 */
    private BigDecimal investShares;

    /** 目标配置比例(%) */
    private BigDecimal targetRatio;

    /** 买入均价（单位净值） */
    private BigDecimal buyPrice;

    /** 首次买入日期 */
    private String buyDate;

    /** 备注 */
    private String remark;
}
