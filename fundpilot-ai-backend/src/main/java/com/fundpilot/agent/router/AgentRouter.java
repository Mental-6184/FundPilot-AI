package com.fundpilot.agent.router;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Agent 路由器
 * 职责：意图识别 → 路由到专业 Agent
 *
 * <p>策略：先用关键词规则快速匹配，命中则直接路由；
 * 未命中则调用 LLM 做意图分类。</p>
 */
@Slf4j
@Component
public class AgentRouter {

    private final ChatClient.Builder chatClientBuilder;

    /** 意图标签枚举 */
    public enum Intent {
        FUND_ANALYSIS,
        FUND_COMPARISON,
        PORTFOLIO_DIAGNOSIS,
        FUND_RECOMMENDATION,
        GENERAL_QA
    }

    /** 关键词规则 */
    private static final Pattern ANALYSIS_PATTERN = Pattern.compile(
            "(分析|查看|看看|详情|怎么样|表现|业绩|风险).*?\\d{6}", Pattern.CASE_INSENSITIVE);

    private static final Pattern COMPARISON_PATTERN = Pattern.compile(
            "(对比|比较|PK|pk|哪个好|哪只|选哪个|区别)", Pattern.CASE_INSENSITIVE);

    private static final Pattern PORTFOLIO_PATTERN = Pattern.compile(
            "(组合|持仓|我的基金|我的投资|诊断|配置)", Pattern.CASE_INSENSITIVE);

    private static final Pattern RECOMMEND_PATTERN = Pattern.compile(
            "(推荐|有什么好的|适合|建议买|配置什么|选什么基金)", Pattern.CASE_INSENSITIVE);

    public AgentRouter(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    /**
     * 路由用户消息到意图
     *
     * @param userMessage 用户消息
     * @return 意图标签
     */
    public Intent route(String userMessage) {
        // 1. 关键词规则匹配（快速路径）
        Intent keywordIntent = matchByKeyword(userMessage);
        if (keywordIntent != null) {
            log.info("[AgentRouter] 关键词匹配: intent={}, message={}", keywordIntent, userMessage);
            return keywordIntent;
        }

        // 2. LLM 意图分类（慢速路径）
        Intent llmIntent = classifyByLLM(userMessage);
        log.info("[AgentRouter] LLM分类: intent={}, message={}", llmIntent, userMessage);
        return llmIntent;
    }

    /**
     * 关键词规则匹配
     */
    private Intent matchByKeyword(String message) {
        if (ANALYSIS_PATTERN.matcher(message).find()) {
            return Intent.FUND_ANALYSIS;
        }
        if (COMPARISON_PATTERN.matcher(message).find()) {
            return Intent.FUND_COMPARISON;
        }
        if (PORTFOLIO_PATTERN.matcher(message).find()) {
            return Intent.PORTFOLIO_DIAGNOSIS;
        }
        if (RECOMMEND_PATTERN.matcher(message).find()) {
            return Intent.FUND_RECOMMENDATION;
        }
        return null;
    }

    /**
     * LLM 意图分类
     */
    private Intent classifyByLLM(String userMessage) {
        try {
            String result = chatClientBuilder
                    .build()
                    .prompt()
                    .system(com.fundpilot.agent.prompt.AgentPrompts.ROUTER_SYSTEM)
                    .user(userMessage)
                    .call()
                    .content();

            return parseIntent(result);
        } catch (Exception e) {
            log.error("[AgentRouter] LLM分类失败，默认 GENERAL_QA", e);
            return Intent.GENERAL_QA;
        }
    }

    /**
     * 解析 LLM 返回的意图标签
     */
    private Intent parseIntent(String llmOutput) {
        if (llmOutput == null) return Intent.GENERAL_QA;
        String cleaned = llmOutput.trim().toUpperCase();
        for (Intent intent : Intent.values()) {
            if (cleaned.contains(intent.name())) {
                return intent;
            }
        }
        return Intent.GENERAL_QA;
    }
}
