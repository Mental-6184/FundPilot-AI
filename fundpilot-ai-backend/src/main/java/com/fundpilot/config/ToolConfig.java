package com.fundpilot.config;

import com.fundpilot.tool.*;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.*;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring AI Tool 注册配置
 * 将 Tool 类的方法包装为 FunctionCallback，注册到 Spring AI 的 Function Calling 机制中
 */
@Configuration
public class ToolConfig {

    // ==================== FundInfoTool ====================

    @Bean
    public FunctionCallback searchFundsCallback(FundInfoTool tool) {
        return FunctionCallbackWrapper.<SearchParams, ToolResult<List<FundListVO>>>builder(
                        (SearchParams params) -> tool.searchFunds(params.keyword(), params.fundType(), params.riskLevel()))
                .withName("searchFunds")
                .withDescription("搜索基金列表。支持按关键词（基金代码或名称）、基金类型、风险等级筛选。返回基金代码、名称、类型、风险等级、最新净值、日收益率等信息。")
                .withInputType(SearchParams.class)
                .build();
    }

    @Bean
    public FunctionCallback getFundDetailCallback(FundInfoTool tool) {
        return FunctionCallbackWrapper.<FundCodeParam, ToolResult<FundDetailVO>>builder(
                        (FundCodeParam params) -> tool.getFundDetail(params.fundCode()))
                .withName("getFundDetail")
                .withDescription("获取单只基金的完整详情。包括基金代码、名称、类型、风险等级、成立日期、规模、费率、最新净值、基金经理等信息。")
                .withInputType(FundCodeParam.class)
                .build();
    }

    @Bean
    public FunctionCallback getFundManagerCallback(FundInfoTool tool) {
        return FunctionCallbackWrapper.<FundCodeParam, ToolResult<ManagerVO>>builder(
                        (FundCodeParam params) -> tool.getFundManager(params.fundCode()))
                .withName("getFundManager")
                .withDescription("获取基金经理的详细信息。包括姓名、所属公司、从业年限、学历、投资风格、最佳任期回报、在管基金数等。")
                .withInputType(FundCodeParam.class)
                .build();
    }

    // ==================== FundReturnTool ====================

    @Bean
    public FunctionCallback getFundReturnCallback(FundReturnTool tool) {
        return FunctionCallbackWrapper.<FundReturnParams, ToolResult<FundPerformanceVO>>builder(
                        (FundReturnParams params) -> tool.getFundReturn(params.fundCode(), params.period()))
                .withName("getFundReturn")
                .withDescription("计算基金的收益率指标。包括累计收益率、年化收益率、近1周/1月/3月/6月/1年/3年收益率。所有数据由Java精确计算。")
                .withInputType(FundReturnParams.class)
                .build();
    }

    @Bean
    public FunctionCallback getNavTrendCallback(FundReturnTool tool) {
        return FunctionCallbackWrapper.<NavTrendParams, ToolResult<FundNavVO>>builder(
                        (NavTrendParams params) -> tool.getNavTrend(params.fundCode(), params.days()))
                .withName("getNavTrend")
                .withDescription("获取基金的净值走势数据。返回指定天数内每天的日期、单位净值、累计净值。适合用于绘制净值走势图表。")
                .withInputType(NavTrendParams.class)
                .build();
    }

    // ==================== FundRiskTool ====================

    @Bean
    public FunctionCallback getRiskMetricsCallback(FundRiskTool tool) {
        return FunctionCallbackWrapper.<RiskMetricsParams, ToolResult<FundPerformanceVO>>builder(
                        (RiskMetricsParams params) -> tool.getRiskMetrics(params.fundCode(), params.benchmarkCode()))
                .withName("getRiskMetrics")
                .withDescription("获取基金的风险指标。包括年化波动率、最大回撤、夏普比率、索提诺比率。可选提供基准指数代码计算Alpha和Beta。所有指标由Java精确计算，非AI编造。")
                .withInputType(RiskMetricsParams.class)
                .build();
    }

    // ==================== FundHoldingTool ====================

    @Bean
    public FunctionCallback getFundHoldingsCallback(FundHoldingTool tool) {
        return FunctionCallbackWrapper.<FundCodeParam, ToolResult<FundHoldingVO>>builder(
                        (FundCodeParam params) -> tool.getFundHoldings(params.fundCode()))
                .withName("getFundHoldings")
                .withDescription("获取基金的持仓信息。返回最新季报的前十大重仓股，包括股票代码、股票名称、持仓占比、持仓市值、持股数量、持仓排名等。")
                .withInputType(FundCodeParam.class)
                .build();
    }

    // ==================== FundCompareTool ====================

    @Bean
    public FunctionCallback compareFundsCallback(FundCompareTool tool) {
        return FunctionCallbackWrapper.<CompareFundsParams, ToolResult<java.util.Map<String, Object>>>builder(
                        (CompareFundsParams params) -> tool.compareFunds(params.fundCodes()))
                .withName("compareFunds")
                .withDescription("对比多只基金的核心指标。传入2-5只基金代码，返回每只基金的基本信息、收益指标、风险指标和最新净值。适合用于基金选择决策。")
                .withInputType(CompareFundsParams.class)
                .build();
    }

    // ==================== PortfolioAnalyzeTool ====================

    @Bean
    public FunctionCallback getUserPortfoliosCallback(PortfolioAnalyzeTool tool) {
        return FunctionCallbackWrapper.<UserPortfoliosParams, ToolResult<List<PortfolioVO>>>builder(
                        (UserPortfoliosParams params) -> tool.getUserPortfolios(params.userId()))
                .withName("getUserPortfolios")
                .withDescription("获取用户的所有投资组合列表。返回组合名称、描述、基金数量、风险等级、总投入金额等信息。")
                .withInputType(UserPortfoliosParams.class)
                .build();
    }

    @Bean
    public FunctionCallback analyzePortfolioCallback(PortfolioAnalyzeTool tool) {
        return FunctionCallbackWrapper.<AnalyzePortfolioParams, ToolResult<PortfolioAnalysisVO>>builder(
                        (AnalyzePortfolioParams params) -> tool.analyzePortfolio(params.userId(), params.portfolioId()))
                .withName("analyzePortfolio")
                .withDescription("深度分析用户的投资组合。返回收益指标、风险指标、风险评分、行业分布、重仓股重合度、持仓明细等分析结果。")
                .withInputType(AnalyzePortfolioParams.class)
                .build();
    }

    // ==================== FundRecommendTool ====================

    @Bean
    public FunctionCallback recommendFundsCallback(FundRecommendTool tool) {
        return FunctionCallbackWrapper.<RecommendFundsParams, ToolResult<List<FundListVO>>>builder(
                        (RecommendFundsParams params) -> tool.recommendFunds(params.fundType(), params.riskLevel(), params.preference(), params.limit()))
                .withName("recommendFunds")
                .withDescription("根据用户偏好推荐基金。支持按基金类型、风险等级筛选，并按收益或风险排序推荐Top N基金。")
                .withInputType(RecommendFundsParams.class)
                .build();
    }

    // ==================== ReportTool ====================

    @Bean
    public FunctionCallback generateFundReportCallback(ReportTool tool) {
        return FunctionCallbackWrapper.<FundReportParams, ToolResult<String>>builder(
                        (FundReportParams params) -> tool.generateFundReport(params.fundCode()))
                .withName("generateFundReport")
                .withDescription("生成基金深度分析报告。包含：摘要、收益分析、风险分析、持仓分析、基金经理分析、优缺点、投资建议。返回Markdown格式的完整报告。")
                .withInputType(FundReportParams.class)
                .build();
    }

    @Bean
    public FunctionCallback generateComparisonReportCallback(ReportTool tool) {
        return FunctionCallbackWrapper.<ComparisonReportParams, ToolResult<String>>builder(
                        (ComparisonReportParams params) -> tool.generateComparisonReport(params.fundCodes()))
                .withName("generateComparisonReport")
                .withDescription("生成基金对比分析报告。横向对比多只基金的收益、风险、持仓等指标。返回Markdown格式的完整报告。")
                .withInputType(ComparisonReportParams.class)
                .build();
    }

    @Bean
    public FunctionCallback generatePortfolioReportCallback(ReportTool tool) {
        return FunctionCallbackWrapper.<PortfolioReportParams, ToolResult<String>>builder(
                        (PortfolioReportParams params) -> tool.generatePortfolioReport(params.userId(), params.portfolioId()))
                .withName("generatePortfolioReport")
                .withDescription("生成投资组合诊断报告。包含：收益分析、风险评分、行业分布、重仓股重合度、优化建议。返回Markdown格式的完整报告。")
                .withInputType(PortfolioReportParams.class)
                .build();
    }

    @Bean
    public FunctionCallback generateRecommendReportCallback(ReportTool tool) {
        return FunctionCallbackWrapper.<RecommendReportParams, ToolResult<String>>builder(
                        (RecommendReportParams params) -> tool.generateRecommendReport(params.demand(), params.fundType(), params.riskLevel(), params.preference()))
                .withName("generateRecommendReport")
                .withDescription("生成基金推荐报告。根据用户需求筛选并推荐基金。返回Markdown格式的完整报告。")
                .withInputType(RecommendReportParams.class)
                .build();
    }

    // ==================== 参数 DTO ====================

    public record SearchParams(String keyword, String fundType, String riskLevel) {}
    public record FundCodeParam(String fundCode) {}
    public record FundReturnParams(String fundCode, String period) {}
    public record NavTrendParams(String fundCode, int days) {}
    public record RiskMetricsParams(String fundCode, String benchmarkCode) {}
    public record CompareFundsParams(List<String> fundCodes) {}
    public record UserPortfoliosParams(Long userId) {}
    public record AnalyzePortfolioParams(Long userId, Long portfolioId) {}
    public record RecommendFundsParams(String fundType, String riskLevel, String preference, int limit) {}
    public record FundReportParams(String fundCode) {}
    public record ComparisonReportParams(String fundCodes) {}
    public record PortfolioReportParams(Long userId, Long portfolioId) {}
    public record RecommendReportParams(String demand, String fundType, String riskLevel, String preference) {}
}
