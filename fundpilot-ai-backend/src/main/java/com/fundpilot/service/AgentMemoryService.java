package com.fundpilot.service;

import com.fundpilot.agent.memory.AgentMemory.ChatMessage;

import java.util.List;

/**
 * Agent 记忆服务接口
 * 管理多轮对话的历史记录和上下文
 */
public interface AgentMemoryService {

    /**
     * 加载会话历史
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 历史消息列表（按时间正序）
     */
    List<ChatMessage> loadHistory(Long userId, String sessionId);

    /**
     * 保存一条消息
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param role      角色（USER / ASSISTANT）
     * @param content   消息内容
     */
    void saveMessage(Long userId, String sessionId, String role, String content);

    /**
     * 组装上下文文本（注入到 Prompt）
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 格式化的上下文文本
     */
    String buildContextText(Long userId, String sessionId);

    /**
     * 获取历史消息数量
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 消息数量
     */
    int getHistoryCount(Long userId, String sessionId);

    /**
     * 清除会话缓存
     *
     * @param sessionId 会话ID
     */
    void evictCache(String sessionId);

    /**
     * 删除会话所有历史记录
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    void clearSession(Long userId, String sessionId);
}
