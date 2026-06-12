package com.fundpilot.tool;

import com.fundpilot.service.ReportService;
import com.fundpilot.tool.dto.ToolResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AI 报告生成 Tool
 * 供 Agent 通过 Tool Calling 生成各类分析报告
 */
@Slf4j
@Component
public class ReportTool {

    private final ReportService reportService;

    public ReportTool(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 生成基金分析报告
     */
    public ToolResult<String> generateFundReport(String fundCode) {
        log.info("[ReportTool] 生成基金分析报告: fundCode={}", fundCode);
        try {
            String report = reportService.generateFundAnalysis(fundCode);
            return ToolResult.success("基金分析报告生成成功", report);
        } catch (Exception e) {
            log.error("[ReportTool] 生成基金分析报告失败: fundCode={}", fundCode, e);
            return ToolResult.fail("生成报告失败: " + e.getMessage());
        }
    }

    /**
     * 生成基金对比报告
     */
    public ToolResult<String> generateComparisonReport(String fundCodes) {
        log.info("[ReportTool] 生成基金对比报告: fundCodes={}", fundCodes);
        try {
            String report = reportService.generateFundComparison(fundCodes);
            return ToolResult.success("基金对比报告生成成功", report);
        } catch (Exception e) {
            log.error("[ReportTool] 生成基金对比报告失败: fundCodes={}", fundCodes, e);
            return ToolResult.fail("生成报告失败: " + e.getMessage());
        }
    }

    /**
     * 生成组合诊断报告
     */
    public ToolResult<String> generatePortfolioReport(Long userId, Long portfolioId) {
        log.info("[ReportTool] 生成组合诊断报告: userId={}, portfolioId={}", userId, portfolioId);
        try {
            String report = reportService.generatePortfolioDiagnosis(userId, portfolioId);
            return ToolResult.success("组合诊断报告生成成功", report);
        } catch (Exception e) {
            log.error("[ReportTool] 生成组合诊断报告失败", e);
            return ToolResult.fail("生成报告失败: " + e.getMessage());
        }
    }

    /**
     * 生成基金推荐报告
     */
    public ToolResult<String> generateRecommendReport(String demand, String fundType,
                                                       String riskLevel, String preference) {
        log.info("[ReportTool] 生成基金推荐报告: demand={}", demand);
        try {
            String report = reportService.generateFundRecommendation(demand, fundType, riskLevel, preference);
            return ToolResult.success("基金推荐报告生成成功", report);
        } catch (Exception e) {
            log.error("[ReportTool] 生成基金推荐报告失败", e);
            return ToolResult.fail("生成报告失败: " + e.getMessage());
        }
    }
}
