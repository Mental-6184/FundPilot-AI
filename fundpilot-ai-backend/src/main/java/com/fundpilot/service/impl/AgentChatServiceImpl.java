package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fundpilot.agent.FundAdvisorAgent;
import com.fundpilot.entity.ChatHistory;
import com.fundpilot.mapper.ChatHistoryMapper;
import com.fundpilot.service.AgentChatService;
import com.fundpilot.service.AgentMemoryService;
import com.fundpilot.vo.ChatResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Agent 对话服务实现
 *
 * <p>完整流程：</p>
 * <pre>
 * 用户消息
 *   │
 *   ├── 1. 会话管理（获取/创建 sessionId）
 *   ├── 2. 加载记忆（AgentMemoryService → Redis/DB）
 *   ├── 3. 委托 Agent（FundAdvisorAgent.chat）
 *   │       ├── 意图路由（AgentRouter）
 *   │       ├── 专业 Agent（Tool Calling）
 *   │       └── 生成回复
 *   ├── 4. 保存记忆（USER + ASSISTANT）
 *   └── 5. 返回响应
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentChatServiceImpl implements AgentChatService {

    private final FundAdvisorAgent fundAdvisorAgent;
    private final AgentMemoryService memoryService;
    private final ChatHistoryMapper chatHistoryMapper;

    @Override
    public ChatResponseVO chat(Long userId, String sessionId, String message) {
        // 1. 会话管理
        String actualSessionId = getOrCreateSessionId(sessionId);
        log.info("[AgentChat] 开始对话: userId={}, sessionId={}, message={}",
                userId, actualSessionId, abbreviate(message, 50));

        long startTime = System.currentTimeMillis();

        try {
            // 2. 委托给主 Agent（Agent 内部完成：记忆加载 → 意图路由 → Tool Calling → 生成回复）
            String reply = fundAdvisorAgent.chat(userId, actualSessionId, message);

            long costMs = System.currentTimeMillis() - startTime;
            log.info("[AgentChat] 对话完成: userId={}, sessionId={}, cost={}ms",
                    userId, actualSessionId, costMs);

            // 3. 构建响应
            return ChatResponseVO.builder()
                    .sessionId(actualSessionId)
                    .content(reply)
                    .done(true)
                    .build();

        } catch (Exception e) {
            long costMs = System.currentTimeMillis() - startTime;
            log.error("[AgentChat] 对话异常: userId={}, sessionId={}, cost={}ms",
                    userId, actualSessionId, costMs, e);

            return ChatResponseVO.builder()
                    .sessionId(actualSessionId)
                    .content("抱歉，处理您的请求时遇到了问题，请稍后重试。错误：" + e.getMessage())
                    .done(true)
                    .build();
        }
    }

    @Override
    public List<String> getUserSessions(Long userId) {
        try {
            List<ChatHistory> sessions = chatHistoryMapper.selectList(
                    new LambdaQueryWrapper<ChatHistory>()
                            .eq(ChatHistory::getUserId, userId)
                            .select(ChatHistory::getSessionId)
                            .groupBy(ChatHistory::getSessionId));

            return sessions.stream()
                    .map(ChatHistory::getSessionId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("[AgentChat] 获取会话列表失败: userId={}", userId, e);
            return List.of();
        }
    }

    @Override
    public void deleteSession(Long userId, String sessionId) {
        memoryService.clearSession(userId, sessionId);
        log.info("[AgentChat] 会话已删除: userId={}, sessionId={}", userId, sessionId);
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取或创建会话ID
     */
    private String getOrCreateSessionId(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return sessionId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 截断字符串（用于日志）
     */
    private String abbreviate(String text, int maxLen) {
        if (text == null) return "null";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
