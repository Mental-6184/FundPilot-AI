package com.fundpilot.agent.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 报告生成数据传输对象
 * 封装传入 LLM 的结构化数据
 */
public class ReportDTO {

    // ========================================================================
    // 1. 基金分析报告输入
    // ========================================================================

    /**
     * 基金分析报告输入数据
     */
    public record FundAnalysisInput(
            String fundCode,
            String fundName,
            String fundType,
            String riskLevel,
            BigDecimal latestNav,
            BigDecimal scale,
            BigDecimal return1w,
            BigDecimal return1m,
            BigDecimal return3m,
            BigDecimal return6m,
            BigDecimal return1y,
            BigDecimal return3y,
            BigDecimal returnSinceEstablish,
            BigDecimal annualizedReturn,
            BigDecimal volatility,
            BigDecimal maxDrawdown,
            BigDecimal sharpeRatio,
            BigDecimal sortinoRatio,
            BigDecimal calmarRatio,
            BigDecimal alpha,
            BigDecimal beta,
            List<HoldingItem> topHoldings,
            String managerName,
            Integer managerYears,
            String managerStyle,
            BigDecimal managerSize
    ) {
        public record HoldingItem(Integer rank, String stockCode, String stockName, BigDecimal holdRatio) {}
    }

    // ========================================================================
    // 2. 基金对比报告输入
    // ========================================================================

    /**
     * 基金对比报告输入数据
     */
    public record FundComparisonInput(
            List<FundCompareItem> funds,
            List<OverlapItem> overlapStocks
    ) {
        public record FundCompareItem(
                String fundCode,
                String fundName,
                String fundType,
                String riskLevel,
                BigDecimal scale,
                BigDecimal latestNav,
                BigDecimal return1w,
                BigDecimal return1m,
                BigDecimal return3m,
                BigDecimal return6m,
                BigDecimal return1y,
                BigDecimal return3y,
                BigDecimal volatility,
                BigDecimal maxDrawdown,
                BigDecimal sharpeRatio,
                BigDecimal sortinoRatio,
                List<FundAnalysisInput.HoldingItem> topHoldings
        ) {}

        public record OverlapItem(String stockCode, String stockName, int appearCount, List<String> fundCodes) {}
    }

    // ========================================================================
    // 3. 组合诊断报告输入
    // ========================================================================

    /**
     * 组合诊断报告输入数据
     */
    public record PortfolioDiagnosisInput(
            Long portfolioId,
            String portfolioName,
            int fundCount,
            BigDecimal totalInvest,
            BigDecimal totalValue,
            BigDecimal totalProfit,
            BigDecimal cumulativeReturn,
            BigDecimal annualizedReturn,
            BigDecimal volatility,
            BigDecimal maxDrawdown,
            BigDecimal sharpeRatio,
            int riskScore,
            String riskLevel,
            String riskLevelName,
            RiskScoreDetail riskScoreDetail,
            List<IndustryItem> industryDistribution,
            int overlapScore,
            int overlapCount,
            List<OverlapDetail> overlapDetails,
            List<FundItem> fundItems
    ) {
        public record RiskScoreDetail(
                int volatilityScore,
                int drawdownScore,
                int concentrationScore,
                int fundRiskScore,
                BigDecimal volatilityWeight,
                BigDecimal drawdownWeight,
                BigDecimal concentrationWeight,
                BigDecimal fundRiskWeight
        ) {}

        public record IndustryItem(String type, BigDecimal ratio, int fundCount) {}

        public record OverlapDetail(String stockCode, String stockName, int appearCount, List<String> fundCodes) {}

        public record FundItem(String fundCode, String fundName, BigDecimal investAmount, BigDecimal actualRatio, BigDecimal profit) {}
    }

    // ========================================================================
    // 4. 基金推荐报告输入
    // ========================================================================

    /**
     * 基金推荐报告输入数据
     */
    public record FundRecommendationInput(
            String userDemand,
            String fundType,
            String riskLevel,
            String preference,
            List<RecommendItem> recommendedFunds
    ) {
        public record RecommendItem(
                String fundCode,
                String fundName,
                String fundType,
                String riskLevel,
                BigDecimal scale,
                BigDecimal latestNav,
                BigDecimal return1w,
                BigDecimal return1m,
                BigDecimal return3m,
                BigDecimal return6m,
                BigDecimal return1y,
                BigDecimal volatility,
                BigDecimal maxDrawdown,
                BigDecimal sharpeRatio,
                String recommendReason
        ) {}
    }
}
