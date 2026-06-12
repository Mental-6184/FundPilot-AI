package com.fundpilot.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 对话响应 VO
 */
@Data
@Builder
public class ChatResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话ID */
    private String sessionId;

    /** AI 回复内容 */
    private String content;

    /** 是否为流式响应的最后一条 */
    private Boolean done;
}
