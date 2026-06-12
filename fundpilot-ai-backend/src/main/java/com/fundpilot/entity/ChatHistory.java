package com.fundpilot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 对话历史实体
 */
@Data
@TableName("chat_history")
public class ChatHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 会话ID */
    private String sessionId;

    /** 消息角色: USER / ASSISTANT / SYSTEM */
    private String role;

    /** 消息正文 */
    private String content;

    /** 消耗的 Token 数 */
    private Integer tokenCount;

    /** 使用的模型名称 */
    private String modelName;

    /** AI 调用的工具列表（JSON） */
    private String toolCalls;

    /** AI 响应耗时（毫秒） */
    private Integer costMs;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
