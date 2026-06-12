package com.fundpilot.tool;

import com.fundpilot.dto.AnalyticsRequestDTO;
import com.fundpilot.service.AnalyticsService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.FundPerformanceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * FundRiskTool — 基金风险指标查询
 * 职责：波动率、最大回撤、夏普比率、索提诺比率、Alpha、Beta
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundRiskTool {

    private final AnalyticsService analyticsService;

    /**
     * 获取基金的风险指标
     */
    @Cacheable(value = "fund:risk", key = "#fundCode + ':' + #benchmarkCode", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<FundPerformanceVO> getRiskMetrics(String fundCode, String benchmarkCode) {
        log.info("[FundRiskTool] getRiskMetrics: fundCode={}, benchmarkCode={}", fundCode, benchmarkCode);
        try {
            AnalyticsRequestDTO request = new AnalyticsRequestDTO();
            request.setFundCode(fundCode);
            request.setPeriod("all");
            request.setBenchmarkCode(benchmarkCode);
            FundPerformanceVO result = analyticsService.analyze(request);
            return ToolResult.ok(result);
        } catch (Exception e) {
            log.error("[FundRiskTool] getRiskMetrics 异常: fundCode={}", fundCode, e);
            return ToolResult.fail("计算风险指标失败: " + e.getMessage());
        }
    }
}
