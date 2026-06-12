package com.fundpilot.service.impl;

import com.fundpilot.agent.FundAdvisorAgent;
import com.fundpilot.dto.ChatRequestDTO;
import com.fundpilot.service.ChatService;
import com.fundpilot.vo.ChatResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * AI 对话服务实现
 *
 * <p>职责：会话管理 + 委托给 FundAdvisorAgent</p>
 * <p>会话生命周期：创建 sessionId → 多轮对话 → 会话过期</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final FundAdvisorAgent fundAdvisorAgent;

    @Override
    public ChatResponseVO chat(Long userId, ChatRequestDTO dto) {
        String sessionId = getOrCreateSessionId(dto.getSessionId());
        log.info("[ChatService] 同步对话: userId={}, sessionId={}", userId, sessionId);

        // 委托给主 Agent（Agent 内部完成：记忆加载 → 意图路由 → 专业Agent → 记忆保存）
        String reply = fundAdvisorAgent.chat(userId, sessionId, dto.getMessage());

        return ChatResponseVO.builder()
                .sessionId(sessionId)
                .content(reply)
                .done(true)
                .build();
    }

    @Override
    public Flux<ChatResponseVO> chatStream(Long userId, ChatRequestDTO dto) {
        String sessionId = getOrCreateSessionId(dto.getSessionId());
        log.info("[ChatService] 流式对话: userId={}, sessionId={}", userId, sessionId);

        return fundAdvisorAgent.chatStream(userId, sessionId, dto.getMessage())
                .map(chunk -> ChatResponseVO.builder()
                        .sessionId(sessionId)
                        .content(chunk)
                        .done(false)
                        .build())
                .concatWithValues(ChatResponseVO.builder()
                        .sessionId(sessionId)
                        .content("")
                        .done(true)
                        .build());
    }

    /**
     * 获取或创建会话ID
     */
    private String getOrCreateSessionId(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return sessionId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
