package com.fundpilot.agent.memory;

/**
 * Agent 记忆模块
 *
 * <p>ChatMessage record 供各模块共享使用</p>
 * <p>实际记忆管理由 {@link com.fundpilot.service.AgentMemoryService} 实现</p>
 */
public final class AgentMemory {

    private AgentMemory() {}

    /**
     * 消息记录
     */
    public record ChatMessage(String role, String content) {}
}
