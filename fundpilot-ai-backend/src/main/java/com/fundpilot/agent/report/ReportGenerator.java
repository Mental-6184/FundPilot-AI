package com.fundpilot.agent.report;

import com.fundpilot.agent.prompt.PromptTemplate;
import com.fundpilot.service.AnalyticsService;
import com.fundpilot.service.FundHoldingService;
import com.fundpilot.service.FundManagerService;
import com.fundpilot.service.FundService;
import com.fundpilot.service.PortfolioAnalysisService;
import com.fundpilot.service.PortfolioService;
import com.fundpilot.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * AI 分析报告生成器
 *
 * <p>职责：</p>
 * <ol>
 *   <li>从 Service 层获取结构化数据</li>
 *   <li>将数据序列化为文本</li>
 *   <li>调用 LLM 生成分析报告</li>
 *   <li>返回完整的 Markdown 报告</li>
 * </ol>
 *
 * <p>支持4种报告类型：基金分析、基金对比、组合诊断、基金推荐</p>
 */
@Slf4j
@Component
public class ReportGenerator {

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private ChatClient chatClient; // 懒初始化
    private final AnalyticsService analyticsService;
    private final FundService fundService;
    private final FundHoldingService fundHoldingService;
    private final FundManagerService fundManagerService;
    private final PortfolioService portfolioService;
    private final PortfolioAnalysisService portfolioAnalysisService;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int SCALE = 4;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;

    public ReportGenerator(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
                           AnalyticsService analyticsService,
                           FundService fundService,
                           FundHoldingService fundHoldingService,
                           FundManagerService fundManagerService,
                           PortfolioService portfolioService,
                           PortfolioAnalysisService portfolioAnalysisService) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.analyticsService = analyticsService;
        this.fundService = fundService;
        this.fundHoldingService = fundHoldingService;
        this.fundManagerService = fundManagerService;
        this.portfolioService = portfolioService;
        this.portfolioAnalysisService = portfolioAnalysisService;
    }

    private ChatClient getChatClient() {
        if (this.chatClient == null) {
            this.chatClient = chatClientBuilderProvider.getObject().build();
        }
        return this.chatClient;
    }

    // ========================================================================
    // 公开接口
    // ========================================================================

    /**
     * 生成基金分析报告
     *
     * @param fundCode 基金代码
     * @return Markdown 格式报告
     */
    public String generateFundAnalysis(String fundCode) {
        log.info("[ReportGenerator] 生成基金分析报告: fundCode={}", fundCode);

        // 1. 获取数据
        FundDetailVO detail = fundService.getFundDetail(fundCode);
        FundPerformanceVO perf = analyticsService.calculateFullPerformance(fundCode);
        FundHoldingVO holding = fundHoldingService.getHoldingData(fundCode);
        ManagerVO manager = fundManagerService.getManagerByFundCode(fundCode);

        // 2. 构建数据文本
        String dataText = buildFundAnalysisData(fundCode, detail, perf, holding, manager);

        // 3. 调用 LLM 生成报告
        String prompt = PromptTemplate.render(ReportPrompts.FUND_ANALYSIS_PLAN,
                Map.of("fundData", dataText));

        return getChatClient().prompt()
                .system(ReportPrompts.FUND_ANALYSIS_SYSTEM)
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 生成基金对比报告
     *
     * @param fundCodes 基金代码列表
     * @return Markdown 格式报告
     */
    public String generateFundComparison(List<String> fundCodes) {
        log.info("[ReportGenerator] 生成基金对比报告: fundCodes={}", fundCodes);

        // 1. 获取每只基金的数据
        List<String> dataLines = new ArrayList<>();
        for (String code : fundCodes) {
            FundDetailVO detail = fundService.getFundDetail(code);
            FundPerformanceVO perf = analyticsService.calculateFullPerformance(code);
            dataLines.add(buildFundCompareItem(code, detail, perf));
        }

        String dataText = String.join("\n---\n", dataLines);

        // 2. 调用 LLM 生成报告
        String prompt = PromptTemplate.render(ReportPrompts.FUND_COMPARISON_PLAN,
                Map.of("comparisonData", dataText));

        return getChatClient().prompt()
                .system(ReportPrompts.FUND_COMPARISON_SYSTEM)
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 生成组合诊断报告
     *
     * @param userId      用户ID
     * @param portfolioId 组合ID
     * @return Markdown 格式报告
     */
    public String generatePortfolioDiagnosis(Long userId, Long portfolioId) {
        log.info("[ReportGenerator] 生成组合诊断报告: userId={}, portfolioId={}", userId, portfolioId);

        // 1. 获取组合分析数据
        PortfolioAnalysisVO analysis = portfolioAnalysisService.analyze(userId, portfolioId);

        // 2. 构建数据文本
        String dataText = buildPortfolioDiagnosisData(analysis);

        // 3. 调用 LLM 生成报告
        String prompt = PromptTemplate.render(ReportPrompts.PORTFOLIO_DIAGNOSIS_PLAN,
                Map.of("portfolioData", dataText));

        return getChatClient().prompt()
                .system(ReportPrompts.PORTFOLIO_DIAGNOSIS_SYSTEM)
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 生成基金推荐报告
     *
     * @param userDemand 用户需求描述
     * @param fundType   偏好类型（可为null）
     * @param riskLevel  偏好风险（可为null）
     * @param preference 排序偏好（RETURN/RISK/BALANCED）
     * @return Markdown 格式报告
     */
    public String generateFundRecommendation(String userDemand, String fundType,
                                              String riskLevel, String preference) {
        log.info("[ReportGenerator] 生成基金推荐报告: demand={}", userDemand);

        // 1. 构建需求文本
        String demandText = buildRecommendDemandText(userDemand, fundType, riskLevel, preference);

        // 2. 调用 LLM 生成报告（LLM 会通过 Tool 获取数据）
        String prompt = PromptTemplate.render(ReportPrompts.FUND_RECOMMENDATION_PLAN,
                Map.of("userDemand", demandText,
                       "recommendData", "请通过 FundRecommendTool 获取候选基金数据，"
                               + "然后通过 FundReturnTool 和 FundRiskTool 获取详细指标。"));

        return getChatClient().prompt()
                .system(ReportPrompts.FUND_RECOMMENDATION_SYSTEM)
                .user(prompt)
                .call()
                .content();
    }

    // ========================================================================
    // 数据序列化
    // ========================================================================

    /**
     * 构建基金分析数据文本
     */
    private String buildFundAnalysisData(String fundCode, FundDetailVO detail,
                                          FundPerformanceVO perf, FundHoldingVO holding,
                                          ManagerVO manager) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 基金基本信息\n");
        sb.append("- 基金代码: ").append(fundCode).append("\n");
        sb.append("- 基金名称: ").append(detail != null ? detail.getFundName() : "N/A").append("\n");
        sb.append("- 基金类型: ").append(detail != null ? detail.getFundType() : "N/A").append("\n");
        sb.append("- 风险等级: ").append(detail != null ? detail.getRiskLevel() : "N/A").append("\n");
        sb.append("- 最新净值: ").append(detail != null ? detail.getLatestNav() : "N/A").append("\n");
        sb.append("- 基金规模: ").append(detail != null ? detail.getScale() : "N/A").append(" 亿元\n");
        sb.append("- 成立日期: ").append(detail != null ? detail.getEstablishDate() : "N/A").append("\n");

        sb.append("\n## 收益数据\n");
        if (perf != null) {
            sb.append("- 近1周: ").append(formatPct(perf.getReturn1w())).append("\n");
            sb.append("- 近1月: ").append(formatPct(perf.getReturn1m())).append("\n");
            sb.append("- 近3月: ").append(formatPct(perf.getReturn3m())).append("\n");
            sb.append("- 近6月: ").append(formatPct(perf.getReturn6m())).append("\n");
            sb.append("- 近1年: ").append(formatPct(perf.getReturn1y())).append("\n");
            sb.append("- 近3年: ").append(formatPct(perf.getReturn3y())).append("\n");
            sb.append("- 成立以来: ").append(formatPct(perf.getReturnSinceEstablish())).append("\n");
            sb.append("- 年化收益率: ").append(formatPct(perf.getAnnualizedReturn())).append("\n");
        }

        sb.append("\n## 风险指标\n");
        if (perf != null) {
            sb.append("- 年化波动率: ").append(formatPct(perf.getAnnualizedVolatility())).append("\n");
            sb.append("- 最大回撤: ").append(formatPct(perf.getMaxDrawdown())).append("\n");
            sb.append("- 夏普比率: ").append(formatDecimal(perf.getSharpeRatio())).append("\n");
            sb.append("- 索提诺比率: ").append(formatDecimal(perf.getSortinoRatio())).append("\n");
            sb.append("- 卡尔马比率: ").append(formatDecimal(perf.getCalmarRatio())).append("\n");
            sb.append("- Alpha: ").append(formatPct(perf.getAlpha())).append("\n");
            sb.append("- Beta: ").append(formatDecimal(perf.getBeta())).append("\n");
        }

        sb.append("\n## 前十大重仓股\n");
        if (holding != null && holding.getTopHoldings() != null) {
            sb.append("| 排名 | 股票代码 | 股票名称 | 持仓占比 |\n");
            sb.append("|------|----------|----------|----------|\n");
            for (var h : holding.getTopHoldings()) {
                sb.append("| ").append(h.getRank())
                  .append(" | ").append(h.getStockCode())
                  .append(" | ").append(h.getStockName())
                  .append(" | ").append(formatPct(h.getHoldRatio())).append(" |\n");
            }
        }

        sb.append("\n## 基金经理\n");
        if (manager != null) {
            sb.append("- 姓名: ").append(manager.getName()).append("\n");
            sb.append("- 从业年限: ").append(manager.getTenureYears()).append(" 年\n");
            sb.append("- 投资风格: ").append(manager.getInvestStyle()).append("\n");
            sb.append("- 在管基金数: ").append(manager.getManageCount()).append("\n");
        }

        return sb.toString();
    }

    /**
     * 构建基金对比数据文本
     */
    private String buildFundCompareItem(String fundCode, FundDetailVO detail,
                                         FundPerformanceVO perf) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 基金 ").append(fundCode).append("\n");
        sb.append("- 名称: ").append(detail != null ? detail.getFundName() : "N/A").append("\n");
        sb.append("- 类型: ").append(detail != null ? detail.getFundType() : "N/A").append("\n");
        sb.append("- 风险等级: ").append(detail != null ? detail.getRiskLevel() : "N/A").append("\n");
        sb.append("- 规模: ").append(detail != null ? detail.getScale() : "N/A").append(" 亿\n");
        sb.append("- 最新净值: ").append(detail != null ? detail.getLatestNav() : "N/A").append("\n");

        if (perf != null) {
            sb.append("- 收益率: 1周=").append(formatPct(perf.getReturn1w()))
              .append(", 1月=").append(formatPct(perf.getReturn1m()))
              .append(", 3月=").append(formatPct(perf.getReturn3m()))
              .append(", 6月=").append(formatPct(perf.getReturn6m()))
              .append(", 1年=").append(formatPct(perf.getReturn1y())).append("\n");
            sb.append("- 风险: 波动率=").append(formatPct(perf.getAnnualizedVolatility()))
              .append(", 最大回撤=").append(formatPct(perf.getMaxDrawdown()))
              .append(", 夏普=").append(formatDecimal(perf.getSharpeRatio())).append("\n");
        }

        return sb.toString();
    }

    /**
     * 构建组合诊断数据文本
     */
    private String buildPortfolioDiagnosisData(PortfolioAnalysisVO analysis) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 组合基本信息\n");
        sb.append("- 组合名称: ").append(analysis.getPortfolioName()).append("\n");
        sb.append("- 基金数量: ").append(analysis.getFundItems() != null ? analysis.getFundItems().size() : 0).append("\n");
        sb.append("- 累计收益率: ").append(formatPct(analysis.getCumulativeReturn())).append("\n");
        sb.append("- 总盈亏: ").append(analysis.getTotalProfit()).append(" 元\n");

        sb.append("\n## 风险指标\n");
        sb.append("- 波动率: ").append(formatPct(analysis.getVolatility())).append("\n");
        sb.append("- 最大回撤: ").append(formatPct(analysis.getMaxDrawdown())).append("\n");
        sb.append("- 夏普比率: ").append(formatDecimal(analysis.getSharpeRatio())).append("\n");

        sb.append("\n## 风险评分\n");
        sb.append("- 综合评分: ").append(analysis.getRiskScore()).append("/100\n");
        sb.append("- 风险等级: ").append(analysis.getRiskLevelName()).append("\n");
        if (analysis.getRiskScoreDetail() != null) {
            var d = analysis.getRiskScoreDetail();
            sb.append("- 波动率得分: ").append(d.getVolatilityScore()).append(" (权重").append(d.getVolatilityWeight()).append(")\n");
            sb.append("- 回撤得分: ").append(d.getDrawdownScore()).append(" (权重").append(d.getDrawdownWeight()).append(")\n");
            sb.append("- 集中度得分: ").append(d.getConcentrationScore()).append(" (权重").append(d.getConcentrationWeight()).append(")\n");
            sb.append("- 基金风险得分: ").append(d.getFundRiskScore()).append(" (权重").append(d.getFundRiskWeight()).append(")\n");
        }

        sb.append("\n## 行业分布\n");
        if (analysis.getIndustryDistribution() != null) {
            sb.append("| 类型 | 占比 | 基金数量 |\n");
            sb.append("|------|------|----------|\n");
            for (var item : analysis.getIndustryDistribution()) {
                sb.append("| ").append(item.getIndustry())
                  .append(" | ").append(formatPct(item.getRatio()))
                  .append(" | ").append(item.getFundCount()).append(" |\n");
            }
        }

        sb.append("\n## 重仓股重合度\n");
        sb.append("- 重合度评分: ").append(analysis.getOverlapScore()).append("%\n");
        if (analysis.getHoldingOverlaps() != null) {
            sb.append("- 重合股票数: ").append(analysis.getHoldingOverlaps().size()).append("\n");
            for (var overlap : analysis.getHoldingOverlaps()) {
                sb.append("  - ").append(overlap.getStockName())
                  .append("(").append(overlap.getStockCode()).append(")")
                  .append(" 出现").append(overlap.getAppearCount()).append("次\n");
            }
        }

        sb.append("\n## 持仓详情\n");
        if (analysis.getFundItems() != null) {
            sb.append("| 基金代码 | 基金名称 | 投入金额 | 占比 |\n");
            sb.append("|----------|----------|----------|------|\n");
            for (var item : analysis.getFundItems()) {
                sb.append("| ").append(item.getFundCode())
                  .append(" | ").append(item.getFundName())
                  .append(" | ").append(item.getInvestAmount())
                  .append(" | ").append(formatPct(item.getActualRatio())).append(" |\n");
            }
        }

        return sb.toString();
    }

    /**
     * 构建推荐需求文本
     */
    private String buildRecommendDemandText(String userDemand, String fundType,
                                             String riskLevel, String preference) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户需求: ").append(userDemand).append("\n");
        if (fundType != null && !fundType.isEmpty()) {
            sb.append("偏好类型: ").append(fundType).append("\n");
        }
        if (riskLevel != null && !riskLevel.isEmpty()) {
            sb.append("偏好风险: ").append(riskLevel).append("\n");
        }
        sb.append("排序偏好: ").append(preference != null ? preference : "BALANCED").append("\n");
        return sb.toString();
    }

    // ========================================================================
    // 格式化工具
    // ========================================================================

    private String formatPct(BigDecimal value) {
        if (value == null) return "N/A";
        return value.setScale(2, ROUND) + "%";
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) return "N/A";
        return value.setScale(SCALE, ROUND).toPlainString();
    }
}
