package com.fundpilot.agent;

import com.fundpilot.agent.prompt.AgentPrompts;
import com.fundpilot.agent.prompt.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 基金分析 Agent
 * 职责：单只基金的深度分析
 *
 * <p>Planner 流程:</p>
 * <ol>
 *   <li>获取基金基本信息</li>
 *   <li>计算各周期收益率</li>
 *   <li>计算风险指标</li>
 *   <li>获取持仓信息</li>
 *   <li>获取基金经理信息</li>
 *   <li>综合生成分析报告</li>
 * </ol>
 */
@Slf4j
@Component
public class FundAnalysisAgent {

    private final ChatClient chatClient;

    public FundAnalysisAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(AgentPrompts.ANALYSIS_SYSTEM)
                .defaultFunctions("searchFunds", "getFundDetail", "getFundManager",
                        "getFundReturn", "getNavTrend", "getRiskMetrics", "getFundHoldings")
                .build();
    }

    /**
     * 执行基金分析
     *
     * @param fundCode  基金代码
     * @param context   上下文文本（对话历史）
     * @return 分析报告
     */
    public String analyze(String fundCode, String context) {
        log.info("[FundAnalysisAgent] 开始分析基金: fundCode={}", fundCode);

        String plannerPrompt = PromptTemplate.render(
                AgentPrompts.ANALYSIS_PLAN,
                Map.of("fundCode", fundCode));

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
