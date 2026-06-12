package com.fundpilot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分析周期枚举
 */
@Getter
@AllArgsConstructor
public enum AnalysisPeriod {

    W1("1w", "近1周", 7),
    M1("1m", "近1月", 30),
    M3("3m", "近3月", 91),
    M6("6m", "近6月", 182),
    Y1("1y", "近1年", 365),
    Y3("3y", "近3年", 1095),
    ALL("all", "成立以来", -1);

    private final String code;
    private final String desc;
    private final int days;

    public static AnalysisPeriod fromCode(String code) {
        for (AnalysisPeriod p : values()) {
            if (p.code.equals(code)) return p;
        }
        return ALL;
    }
}
