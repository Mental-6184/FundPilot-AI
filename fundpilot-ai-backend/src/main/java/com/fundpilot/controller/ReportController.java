package com.fundpilot.controller;

import com.fundpilot.common.result.R;
import com.fundpilot.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI 分析报告接口
 */
@Tag(name = "AI分析报告", description = "AI生成各类分析报告")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 生成基金分析报告
     * GET /api/report/fund/{fundCode}
     */
    @Operation(summary = "生成基金分析报告")
    @GetMapping("/fund/{fundCode}")
    public R<String> fundAnalysis(
            @Parameter(description = "基金代码") @PathVariable String fundCode) {
        String report = reportService.generateFundAnalysis(fundCode);
        return R.ok(report);
    }

    /**
     * 生成基金对比报告
     * GET /api/report/compare?codes=110011,000001,005827
     */
    @Operation(summary = "生成基金对比报告")
    @GetMapping("/compare")
    public R<String> fundComparison(
            @Parameter(description = "基金代码列表，逗号分隔") @RequestParam String codes) {
        String report = reportService.generateFundComparison(codes);
        return R.ok(report);
    }

    /**
     * 生成组合诊断报告
     * GET /api/report/portfolio/{portfolioId}
     */
    @Operation(summary = "生成组合诊断报告")
    @GetMapping("/portfolio/{portfolioId}")
    public R<String> portfolioDiagnosis(
            @Parameter(description = "组合ID") @PathVariable Long portfolioId) {
        // TODO: 从 Sa-Token 获取当前用户ID
        Long userId = 1L;
        String report = reportService.generatePortfolioDiagnosis(userId, portfolioId);
        return R.ok(report);
    }

    /**
     * 生成基金推荐报告
     * GET /api/report/recommend?demand=稳健型&type=BOND&risk=LOW&preference=RISK
     */
    @Operation(summary = "生成基金推荐报告")
    @GetMapping("/recommend")
    public R<String> fundRecommendation(
            @Parameter(description = "用户需求") @RequestParam String demand,
            @Parameter(description = "偏好类型") @RequestParam(required = false) String type,
            @Parameter(description = "偏好风险等级") @RequestParam(required = false) String risk,
            @Parameter(description = "排序偏好: RETURN/RISK/BALANCED") @RequestParam(defaultValue = "BALANCED") String preference) {
        String report = reportService.generateFundRecommendation(demand, type, risk, preference);
        return R.ok(report);
    }
}
