package com.fundpilot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 基金分析请求 DTO
 */
@Data
public class AnalyticsRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 基金代码 */
    @NotBlank(message = "基金代码不能为空")
    private String fundCode;

    /** 分析周期: 1w/1m/3m/6m/1y/3y/all，默认 all */
    private String period = "all";

    /** 无风险利率（%），默认 2.0（1年期国债） */
    private BigDecimal riskFreeRate = new BigDecimal("2.0000");

    /** 基准指数代码（用于计算 Alpha/Beta），可选 */
    private String benchmarkCode;
}
