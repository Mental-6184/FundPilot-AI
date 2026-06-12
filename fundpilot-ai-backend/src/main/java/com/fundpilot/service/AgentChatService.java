package com.fundpilot.service;

import com.fundpilot.vo.ChatResponseVO;

/**
 * Agent 对话服务接口
 * 封装完整的 Agent 对话流程：记忆 → 路由 → 工具调用 → 回复 → 保存
 */
public interface AgentChatService {

    /**
     * 同步对话
     *
     * @param userId    用户ID
     * @param sessionId 会话ID（为空则创建新会话）
     * @param message   用户消息
     * @return AI 回复
     */
    ChatResponseVO chat(Long userId, String sessionId, String message);

    /**
     * 获取用户的历史会话列表
     *
     * @param userId 用户ID
     * @return 会话ID列表
     */
    java.util.List<String> getUserSessions(Long userId);

    /**
     * 删除会话
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    void deleteSession(Long userId, String sessionId);
}
