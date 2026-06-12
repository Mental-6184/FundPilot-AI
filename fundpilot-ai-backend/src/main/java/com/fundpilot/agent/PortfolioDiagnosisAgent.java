package com.fundpilot.agent;

import com.fundpilot.agent.prompt.AgentPrompts;
import com.fundpilot.agent.prompt.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 组合诊断 Agent
 * 职责：投资组合的全面诊断和优化建议
 *
 * <p>Planner 流程:</p>
 * <ol>
 *   <li>获取用户组合列表</li>
 *   <li>获取组合深度分析数据</li>
 *   <li>分析收益指标</li>
 *   <li>分析风险指标</li>
 *   <li>分析风险评分</li>
 *   <li>分析行业分布和重仓股重合度</li>
 *   <li>生成诊断报告和优化建议</li>
 * </ol>
 */
@Slf4j
@Component
public class PortfolioDiagnosisAgent {

    private final ChatClient chatClient;

    public PortfolioDiagnosisAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(AgentPrompts.DIAGNOSIS_SYSTEM)
                .defaultFunctions("getUserPortfolios", "analyzePortfolio",
                        "searchFunds", "getFundReturn")
                .build();
    }

    /**
     * 执行组合诊断
     *
     * @param userId      用户ID
     * @param portfolioId 组合ID
     * @param context     上下文文本
     * @return 诊断报告
     */
    public String diagnose(Long userId, Long portfolioId, String context) {
        log.info("[PortfolioDiagnosisAgent] 开始诊断组合: userId={}, portfolioId={}", userId, portfolioId);

        String plannerPrompt = PromptTemplate.render(
                AgentPrompts.DIAGNOSIS_PLAN,
                Map.of(
                        "userId", String.valueOf(userId),
                        "portfolioId", String.valueOf(portfolioId)));

        String userMessage = plannerPrompt;
        if (context != null && !context.isEmpty()) {
            userMessage = context + "\n\n" + plannerPrompt;
        }

        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
