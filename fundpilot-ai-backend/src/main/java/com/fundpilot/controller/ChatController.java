package com.fundpilot.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fundpilot.common.result.R;
import com.fundpilot.dto.ChatRequestDTO;
import com.fundpilot.service.ChatService;
import com.fundpilot.vo.ChatResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI 对话控制器
 */
@Tag(name = "AI 对话", description = "基金智能分析对话接口")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "同步对话")
    @PostMapping("/send")
    public R<ChatResponseVO> chat(@Valid @RequestBody ChatRequestDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(chatService.chat(userId, dto));
    }

    @Operation(summary = "流式对话 (SSE)")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVO> chatStream(@Valid @RequestBody ChatRequestDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return chatService.chatStream(userId, dto);
    }
}
