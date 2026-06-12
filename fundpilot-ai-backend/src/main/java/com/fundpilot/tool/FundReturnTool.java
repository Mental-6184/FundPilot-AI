package com.fundpilot.tool;

import com.fundpilot.dto.AnalyticsRequestDTO;
import com.fundpilot.service.AnalyticsService;
import com.fundpilot.service.FundNavService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.FundNavVO;
import com.fundpilot.vo.FundPerformanceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * FundReturnTool — 基金收益率查询
 * 职责：累计收益率、年化收益率、各周期收益率、净值走势
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundReturnTool {

    private final AnalyticsService analyticsService;
    private final FundNavService fundNavService;

    /**
     * 计算基金的收益率指标
     */
    @Cacheable(value = "fund:return", key = "#fundCode + ':' + #period", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<FundPerformanceVO> getFundReturn(String fundCode, String period) {
        log.info("[FundReturnTool] getFundReturn: fundCode={}, period={}", fundCode, period);
        try {
            AnalyticsRequestDTO request = new AnalyticsRequestDTO();
            request.setFundCode(fundCode);
            request.setPeriod(period != null ? period : "all");
            FundPerformanceVO result = analyticsService.analyze(request);
            return ToolResult.ok(result);
        } catch (Exception e) {
            log.error("[FundReturnTool] getFundReturn 异常: fundCode={}", fundCode, e);
            return ToolResult.fail("计算收益率失败: " + e.getMessage());
        }
    }

    /**
     * 获取基金的净值走势数据
     */
    @Cacheable(value = "fund:nav", key = "#fundCode + ':' + #days", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<FundNavVO> getNavTrend(String fundCode, int days) {
        log.info("[FundReturnTool] getNavTrend: fundCode={}, days={}", fundCode, days);
        try {
            FundNavVO navData = fundNavService.getNavData(fundCode, days);
            return ToolResult.ok(navData);
        } catch (Exception e) {
            log.error("[FundReturnTool] getNavTrend 异常: fundCode={}", fundCode, e);
            return ToolResult.fail("查询净值走势失败: " + e.getMessage());
        }
    }
}
