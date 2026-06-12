package com.fundpilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 对话请求 DTO
 */
@Data
public class ChatRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话ID (为空则创建新会话) */
    private String sessionId;

    /** 用户消息 */
    @NotBlank(message = "消息不能为空")
    @Size(max = 2000, message = "消息不超过2000个字符")
    private String message;
}
