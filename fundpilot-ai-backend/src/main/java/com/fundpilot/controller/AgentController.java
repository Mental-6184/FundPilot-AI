package com.fundpilot.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fundpilot.common.result.R;
import com.fundpilot.service.AgentChatService;
import com.fundpilot.vo.ChatResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI Agent 对话接口
 *
 * <p>核心接口：POST /agent/chat</p>
 * <p>支持多轮对话、历史记录、上下文记忆</p>
 */
@Tag(name = "AI Agent", description = "基金智能分析 Agent 对话接口")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentChatService agentChatService;

    /**
     * Agent 对话（核心接口）
     *
     * <p>功能：</p>
     * <ul>
     *   <li>多轮对话：传入 sessionId 继续会话，不传则创建新会话</li>
     *   <li>意图路由：自动识别用户意图（分析/对比/诊断/推荐/问答）</li>
     *   <li>Tool Calling：Agent 自动调用相关工具获取数据</li>
     *   <li>上下文记忆：自动加载历史对话作为上下文</li>
     * </ul>
     *
     * <p>示例请求：</p>
     * <pre>
     * {"message": "分析005827"}
     * {"message": "比较005827和163406", "sessionId": "abc123"}
     * {"message": "优化我的基金组合", "sessionId": "abc123"}
     * </pre>
     */
    @Operation(summary = "Agent 对话", description = "支持多轮对话、自动意图识别、Tool Calling")
    @PostMapping("/chat")
    public R<ChatResponseVO> chat(@RequestBody AgentChatRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        ChatResponseVO response = agentChatService.chat(
                userId, request.getSessionId(), request.getMessage());
        return R.ok(response);
    }

    /**
     * 获取用户的会话列表
     */
    @Operation(summary = "获取会话列表")
    @GetMapping("/sessions")
    public R<List<String>> getSessions() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(agentChatService.getUserSessions(userId));
    }

    /**
     * 删除会话（清除历史记录）
     */
    @Operation(summary = "删除会话")
    @DeleteMapping("/sessions/{sessionId}")
    public R<Void> deleteSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        Long userId = StpUtil.getLoginIdAsLong();
        agentChatService.deleteSession(userId, sessionId);
        return R.ok();
    }

    // ==================== 请求体 ====================

    /**
     * Agent 对话请求
     */
    @Data
    public static class AgentChatRequest {

        /** 用户消息（必填） */
        private String message;

        /** 会话ID（可选，为空则创建新会话） */
        private String sessionId;
    }
}
