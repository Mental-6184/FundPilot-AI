package com.fundpilot.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 行业分布 VO
 */
@Data
public class IndustryDistributionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 行业名称 */
    private String industry;

    /** 占比(%) */
    private BigDecimal ratio;

    /** 涉及基金数量 */
    private Integer fundCount;
}
