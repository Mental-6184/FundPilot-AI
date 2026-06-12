package com.fundpilot.agent;

import com.fundpilot.agent.prompt.AgentPrompts;
import com.fundpilot.agent.prompt.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 基金推荐 Agent
 * 职责：根据用户偏好推荐基金
 *
 * <p>Planner 流程:</p>
 * <ol>
 *   <li>分析用户的需求和偏好</li>
 *   <li>筛选候选基金</li>
 *   <li>获取候选基金的收益和风险指标</li>
 *   <li>综合评估，给出推荐排名</li>
 *   <li>说明推荐理由</li>
 * </ol>
 */
@Slf4j
@Component
public class FundRecommendationAgent {

    private final ChatClient chatClient;

    public FundRecommendationAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(AgentPrompts.RECOMMENDATION_SYSTEM)
                .defaultFunctions("searchFunds", "getFundReturn", "getRiskMetrics", "recommendFunds")
                .build();
    }

    /**
     * 执行基金推荐
     *
     * @param userMessage 用户原始消息
     * @param fundType    偏好基金类型
     * @param riskLevel   偏好风险等级
     * @param preference  排序偏好
     * @param context     上下文文本
     * @return 推荐报告
     */
    public String recommend(String userMessage, String fundType, String riskLevel,
                            String preference, String context) {
        log.info("[FundRecommendationAgent] 开始推荐: fundType={}, riskLevel={}, preference={}",
                fundType, riskLevel, preference);

        String plannerPrompt = PromptTemplate.render(
                AgentPrompts.RECOMMENDATION_PLAN,
                Map.of(
                        "userMessage", userMessage != null ? userMessage : "",
                        "fundType", fundType != null ? fundType : "不限",
                        "riskLevel", riskLevel != null ? riskLevel : "不限",
                        "preference", preference != null ? preference : "BALANCED"));

        String finalMessage = plannerPrompt;
        if (context != null && !context.isEmpty()) {
            finalMessage = context + "\n\n" + plannerPrompt;
        }

        return chatClient.prompt()
                .user(finalMessage)
                .call()
                .content();
    }
}
