package com.fundpilot.common.constant;

/**
 * AI 相关常量
 */
public final class AiConstant {

    private AiConstant() {}

    /** 默认系统提示词 */
    public static final String DEFAULT_SYSTEM_PROMPT = """
            你是 FundPilot AI，专业的基金投资分析助手。
            你的职责：
            1. 理解用户的基金投资相关问题
            2. 通过工具获取真实的基金数据（禁止编造数据）
            3. 基于 Java 计算的精确数据给出分析建议
            4. 生成专业、易懂的分析报告

            规则：
            - 所有基金数据必须通过工具获取，禁止凭空编造
            - 数字必须精确，保留合理小数位
            - 分析结论必须基于真实数据
            - 提醒用户投资有风险
            """;

    /** 模型名称 */
    public static final String MODEL_QWEN_MAX = "qwen-max";

    /** 最大 Token 数 */
    public static final int MAX_TOKENS = 4096;

    /** 温度参数 */
    public static final double TEMPERATURE = 0.7;
}
