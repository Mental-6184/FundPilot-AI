package com.fundpilot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 风险等级枚举
 */
@Getter
@AllArgsConstructor
public enum RiskLevel {

    LOW("LOW", "低风险", 1),
    MEDIUM_LOW("MEDIUM_LOW", "中低风险", 2),
    MEDIUM("MEDIUM", "中风险", 3),
    MEDIUM_HIGH("MEDIUM_HIGH", "中高风险", 4),
    HIGH("HIGH", "高风险", 5);

    private final String code;
    private final String desc;
    private final int level;

    /**
     * 根据 code 获取枚举
     */
    public static RiskLevel fromCode(String code) {
        for (RiskLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("未知的风险等级: " + code);
    }
}
