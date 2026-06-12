package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分析报告实体（Java 计算 + LLM 生成）
 */
@Data
@TableName("analysis_report")
public class AnalysisReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发起用户ID */
    private Long userId;

    /** 报告类型: FUND_ANALYSIS / COMPARISON / PORTFOLIO / MARKET */
    private String reportType;

    /** 分析目标代码 */
    private String targetCode;

    /** 报告标题 */
    private String title;

    /** AI 生成的分析摘要 */
    private String summary;

    /** Java 计算的核心指标快照（JSON） */
    private String metricsJson;

    /** AI 生成的完整分析报告（Markdown） */
    private String fullReport;

    /** 使用的模型名称 */
    private String modelName;

    /** 消耗的 Token 总数 */
    private Integer tokenCount;

    /** 生成耗时（毫秒） */
    private Integer costMs;

    /** 状态: 0-已失效 1-有效 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
