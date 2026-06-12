package com.fundpilot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 基金类型枚举
 */
@Getter
@AllArgsConstructor
public enum FundType {

    EQUITY("EQUITY", "股票型"),
    BOND("BOND", "债券型"),
    HYBRID("HYBRID", "混合型"),
    MONEY("MONEY", "货币型"),
    INDEX("INDEX", "指数型"),
    QDII("QDII", "QDII型");

    private final String code;
    private final String desc;

    /**
     * 根据 code 获取枚举
     */
    public static FundType fromCode(String code) {
        for (FundType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的基金类型: " + code);
    }
}
