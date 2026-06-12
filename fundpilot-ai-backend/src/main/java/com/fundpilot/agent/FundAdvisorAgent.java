package com.fundpilot.agent;

import com.fundpilot.agent.prompt.AgentPrompts;
import com.fundpilot.agent.router.AgentRouter;
import com.fundpilot.agent.router.AgentRouter.Intent;
import com.fundpilot.service.AgentMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主 Agent 编排器
 * 职责：意图识别 → 路由到专业 Agent → 管理上下文
 *
 * <p>架构角色: Orchestrator</p>
 * <ul>
 *   <li>接收用户消息</li>
 *   <li>通过 AgentRouter 识别意图</li>
 *   <li>通过 AgentMemory 加载上下文</li>
 *   <li>委托给 4 个专业 Agent 处理</li>
 *   <li>保存回复到 Memory</li>
 * </ul>
 */
@Slf4j
@Component
public class FundAdvisorAgent {

    private final AgentRouter router;
    private final AgentMemoryService memory;
    private final FundAnalysisAgent analysisAgent;
    private final FundComparisonAgent comparisonAgent;
    private final PortfolioDiagnosisAgent diagnosisAgent;
    private final FundRecommendationAgent recommendationAgent;
    private final ChatClient generalChatClient;

    /** 基金代码正则 */
    private static final Pattern FUND_CODE_PATTERN = Pattern.compile("\\d{6}");

    public FundAdvisorAgent(AgentRouter router,
                            AgentMemoryService memory,
                            FundAnalysisAgent analysisAgent,
                            FundComparisonAgent comparisonAgent,
                            PortfolioDiagnosisAgent diagnosisAgent,
                            FundRecommendationAgent recommendationAgent,
                            ChatClient.Builder chatClientBuilder) {
        this.router = router;
        this.memory = memory;
        this.analysisAgent = analysisAgent;
        this.comparisonAgent = comparisonAgent;
        this.diagnosisAgent = diagnosisAgent;
        this.recommendationAgent = recommendationAgent;
        this.generalChatClient = chatClientBuilder
                .defaultSystem(AgentPrompts.GENERAL_QA_SYSTEM)
                .build();
    }

    /**
     * 同步对话（完整流程）
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param message   用户消息
     * @return AI 回复
     */
    public String chat(Long userId, String sessionId, String message) {
        log.info("[FundAdvisorAgent] 开始处理: userId={}, sessionId={}, message={}",
                userId, sessionId, message);

        // 1. 加载上下文
        String context = memory.buildContextText(userId, sessionId);

        // 2. 意图路由
        Intent intent = router.route(message);
        log.info("[FundAdvisorAgent] 意图识别: intent={}", intent);

        // 3. 委托给专业 Agent
        String reply = dispatch(intent, message, userId, context);

        // 4. 保存到记忆
        memory.saveMessage(userId, sessionId, "USER", message);
        memory.saveMessage(userId, sessionId, "ASSISTANT", reply);

        return reply;
    }

    /**
     * 流式对话
     */
    public Flux<String> chatStream(Long userId, String sessionId, String message) {
        // 流式模式下简化处理
        return Flux.defer(() -> {
            String reply = chat(userId, sessionId, message);
            return Flux.just(reply);
        });
    }

    /**
     * 分发到专业 Agent
     */
    private String dispatch(Intent intent, String message, Long userId, String context) {
        try {
            return switch (intent) {
                case FUND_ANALYSIS -> handleAnalysis(message, context);
                case FUND_COMPARISON -> handleComparison(message, context);
                case PORTFOLIO_DIAGNOSIS -> handleDiagnosis(message, userId, context);
                case FUND_RECOMMENDATION -> handleRecommendation(message, context);
                case GENERAL_QA -> handleGeneralQA(message, context);
            };
        } catch (Exception e) {
            log.error("[FundAdvisorAgent] Agent处理异常: intent={}", intent, e);
            return "抱歉，处理您的请求时遇到了问题，请稍后重试。错误信息：" + e.getMessage();
        }
    }

    /**
     * 基金分析：从消息中提取基金代码
     */
    private String handleAnalysis(String message, String context) {
        String fundCode = extractFundCode(message);
        if (fundCode == null) {
            return "请提供基金代码，例如：分析一下基金 110011";
        }
        return analysisAgent.analyze(fundCode, context);
    }

    /**
     * 基金对比：从消息中提取多个基金代码
     */
    private String handleComparison(String message, String context) {
        List<String> fundCodes = extractFundCodes(message);
        if (fundCodes.size() < 2) {
            return "请提供至少2只基金代码进行对比，例如：对比 110011 和 000001";
        }
        return comparisonAgent.compare(fundCodes, context);
    }

    /**
     * 组合诊断：需要用户ID
     */
    private String handleDiagnosis(String message, Long userId, String context) {
        // 尝试从消息中提取组合ID
        Long portfolioId = extractPortfolioId(message);
        if (portfolioId == null) {
            return diagnosisAgent.diagnose(userId, null, context);
        }
        return diagnosisAgent.diagnose(userId, portfolioId, context);
    }

    /**
     * 基金推荐
     */
    private String handleRecommendation(String message, String context) {
        return recommendationAgent.recommend(message, null, null, "BALANCED", context);
    }

    /**
     * 通用问答
     */
    private String handleGeneralQA(String message, String context) {
        String userMessage = message;
        if (context != null && !context.isEmpty()) {
            userMessage = context + "\n\n用户问题: " + message;
        }
        return generalChatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    // ==================== 辅助方法 ====================

    /**
     * 从消息中提取第一个基金代码
     */
    private String extractFundCode(String message) {
        Matcher matcher = FUND_CODE_PATTERN.matcher(message);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * 从消息中提取所有基金代码
     */
    private List<String> extractFundCodes(String message) {
        Matcher matcher = FUND_CODE_PATTERN.matcher(message);
        return matcher.results()
                .map(m -> m.group())
                .distinct()
                .toList();
    }

    /**
     * 从消息中提取组合ID
     */
    private Long extractPortfolioId(String message) {
        // 匹配 "组合ID: 123" 或 "组合 123" 或 "portfolioId=123"
        Pattern pattern = Pattern.compile("(?:组合ID[:\\s=]|组合[:\\s=]|portfolioId[:\\s=]?)(\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }
}
