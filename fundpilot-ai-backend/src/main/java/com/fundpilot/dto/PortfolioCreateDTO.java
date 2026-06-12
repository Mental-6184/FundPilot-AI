package com.fundpilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建组合 DTO
 */
@Data
public class PortfolioCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 组合名称 */
    @NotBlank(message = "组合名称不能为空")
    @Size(max = 50, message = "组合名称不超过50个字符")
    private String portfolioName;

    /** 组合描述 */
    @Size(max = 500, message = "描述不超过500个字符")
    private String description;

    /** 组合整体风险等级 */
    private String riskLevel;

    /** 组合总投入金额（元） */
    private BigDecimal totalAmount;
}
