package com.fundpilot.service;

import com.fundpilot.dto.ChatRequestDTO;
import com.fundpilot.vo.ChatResponseVO;
import reactor.core.publisher.Flux;

/**
 * AI 对话服务接口
 */
public interface ChatService {

    /**
     * 同步对话
     */
    ChatResponseVO chat(Long userId, ChatRequestDTO dto);

    /**
     * 流式对话 (SSE)
     */
    Flux<ChatResponseVO> chatStream(Long userId, ChatRequestDTO dto);
}
