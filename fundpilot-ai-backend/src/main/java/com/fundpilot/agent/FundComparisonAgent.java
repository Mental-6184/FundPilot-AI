package com.fundpilot.agent;

import com.fundpilot.agent.prompt.AgentPrompts;
import com.fundpilot.agent.prompt.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 基金对比 Agent
 * 职责：多只基金的横向对比分析
 *
 * <p>Planner 流程:</p>
 * <ol>
 *   <li>获取每只基金的基本信息</li>
 *   <li>获取每只基金的收益率</li>
 *   <li>获取每只基金的风险指标</li>
 *   <li>生成对比表格和结论</li>
 * </ol>
 */
@Slf4j
@Component
public class FundComparisonAgent {

    private final ChatClient chatClient;

    public FundComparisonAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(AgentPrompts.COMPARISON_SYSTEM)
                .defaultFunctions("searchFunds", "getFundDetail", "getFundReturn",
                        "getRiskMetrics", "compareFunds")
                .build();
    }

    /**
     * 执行基金对比
     *
     * @param fundCodes 基金代码列表
     * @param context   上下文文本
     * @return 对比报告
     */
    public String compare(List<String> fundCodes, String context) {
        log.info("[FundComparisonAgent] 开始对比基金: fundCodes={}", fundCodes);

        String plannerPrompt = PromptTemplate.render(
                AgentPrompts.COMPARISON_PLAN,
                Map.of("fundCodes", String.join(", ", fundCodes)));

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
