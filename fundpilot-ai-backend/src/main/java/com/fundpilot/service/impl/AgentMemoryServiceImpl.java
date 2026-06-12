package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fundpilot.agent.memory.AgentMemory;
import com.fundpilot.agent.memory.AgentMemory.ChatMessage;
import com.fundpilot.entity.ChatHistory;
import com.fundpilot.mapper.ChatHistoryMapper;
import com.fundpilot.service.AgentMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Agent 记忆服务实现
 *
 * <p>双层存储架构：</p>
 * <ol>
 *   <li>Redis 缓存层：热数据，TTL 2小时</li>
 *   <li>MySQL 持久层：全量数据，永久保存</li>
 * </ol>
 *
 * <p>读取路径：Redis → MySQL → 回填缓存</p>
 * <p>写入路径：MySQL → 更新缓存</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentMemoryServiceImpl implements AgentMemoryService {

    private final ChatHistoryMapper chatHistoryMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String SESSION_CACHE_PREFIX = "agent:session:";
    private static final String COUNT_CACHE_PREFIX = "agent:count:";
    private static final int MAX_HISTORY_MESSAGES = 20;
    private static final long CACHE_TTL_HOURS = 2;

    @Override
    public List<ChatMessage> loadHistory(Long userId, String sessionId) {
        String cacheKey = SESSION_CACHE_PREFIX + sessionId;

        // 1. 尝试从 Redis 读取
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("[MemoryService] 缓存命中: sessionId={}", sessionId);
                return deserializeMessages(cached);
            }
        } catch (Exception e) {
            log.warn("[MemoryService] Redis 读取失败，降级到 DB", e);
        }

        // 2. 从 DB 加载最近 N 条
        List<ChatHistory> histories = chatHistoryMapper.selectList(
                new LambdaQueryWrapper<ChatHistory>()
                        .eq(ChatHistory::getUserId, userId)
                        .eq(ChatHistory::getSessionId, sessionId)
                        .orderByAsc(ChatHistory::getCreateTime)
                        .last("LIMIT " + MAX_HISTORY_MESSAGES));

        List<ChatMessage> messages = histories.stream()
                .map(h -> new ChatMessage(h.getRole(), h.getContent()))
                .collect(Collectors.toList());

        // 3. 回填缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, serializeMessages(messages),
                    CACHE_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("[MemoryService] 缓存写入失败", e);
        }

        return messages;
    }

    @Override
    public void saveMessage(Long userId, String sessionId, String role, String content) {
        // 1. 写入 DB
        ChatHistory history = new ChatHistory();
        history.setUserId(userId);
        history.setSessionId(sessionId);
        history.setRole(role);
        history.setContent(content);
        chatHistoryMapper.insert(history);

        // 2. 更新缓存
        try {
            String cacheKey = SESSION_CACHE_PREFIX + sessionId;
            List<ChatMessage> messages = loadHistory(userId, sessionId);
            messages.add(new ChatMessage(role, content));
            // 保留最近 N 条
            if (messages.size() > MAX_HISTORY_MESSAGES) {
                messages = new ArrayList<>(
                        messages.subList(messages.size() - MAX_HISTORY_MESSAGES, messages.size()));
            }
            redisTemplate.opsForValue().set(cacheKey, serializeMessages(messages),
                    CACHE_TTL_HOURS, TimeUnit.HOURS);

            // 更新计数缓存
            String countKey = COUNT_CACHE_PREFIX + sessionId;
            redisTemplate.opsForValue().set(countKey, String.valueOf(messages.size()),
                    CACHE_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("[MemoryService] 缓存更新失败", e);
        }

        log.debug("[MemoryService] 消息已保存: role={}, sessionId={}", role, sessionId);
    }

    @Override
    public String buildContextText(Long userId, String sessionId) {
        List<ChatMessage> history = loadHistory(userId, sessionId);
        if (history.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## 对话历史（最近 ").append(history.size()).append(" 条）\n");
        for (ChatMessage msg : history) {
            String roleLabel = "USER".equals(msg.role()) ? "用户" : "AI助手";
            sb.append("- ").append(roleLabel).append(": ").append(msg.content()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int getHistoryCount(Long userId, String sessionId) {
        String countKey = COUNT_CACHE_PREFIX + sessionId;
        try {
            String cached = redisTemplate.opsForValue().get(countKey);
            if (cached != null) {
                return Integer.parseInt(cached);
            }
        } catch (Exception e) {
            log.warn("[MemoryService] 计数缓存读取失败", e);
        }

        // 从 DB 查询
        Long count = chatHistoryMapper.selectCount(
                new LambdaQueryWrapper<ChatHistory>()
                        .eq(ChatHistory::getUserId, userId)
                        .eq(ChatHistory::getSessionId, sessionId));
        return count != null ? count.intValue() : 0;
    }

    @Override
    public void evictCache(String sessionId) {
        try {
            redisTemplate.delete(SESSION_CACHE_PREFIX + sessionId);
            redisTemplate.delete(COUNT_CACHE_PREFIX + sessionId);
            log.debug("[MemoryService] 缓存已清除: sessionId={}", sessionId);
        } catch (Exception e) {
            log.warn("[MemoryService] 缓存清除失败", e);
        }
    }

    @Override
    public void clearSession(Long userId, String sessionId) {
        // 1. 清除缓存
        evictCache(sessionId);

        // 2. 逻辑删除 DB 记录
        chatHistoryMapper.delete(
                new LambdaQueryWrapper<ChatHistory>()
                        .eq(ChatHistory::getUserId, userId)
                        .eq(ChatHistory::getSessionId, sessionId));

        log.info("[MemoryService] 会话已清除: userId={}, sessionId={}", userId, sessionId);
    }

    // ==================== 序列化 ====================

    private String serializeMessages(List<ChatMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            // 用  分隔 role 和 content，避免内容中的 | 导致解析错误
            sb.append(msg.role()).append('').append(msg.content()).append('\n');
        }
        return sb.toString();
    }

    private List<ChatMessage> deserializeMessages(String cached) {
        List<ChatMessage> messages = new ArrayList<>();
        for (String line : cached.split("\n")) {
            int idx = line.indexOf('');
            if (idx > 0) {
                messages.add(new ChatMessage(line.substring(0, idx), line.substring(idx + 1)));
            }
        }
        return messages;
    }
}
