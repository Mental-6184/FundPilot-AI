package com.fundpilot.tool;

import com.fundpilot.service.AnalyticsService;
import com.fundpilot.service.FundNavService;
import com.fundpilot.service.FundService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * FundCompareTool — 基金对比
 * 职责：多只基金的收益、风险、持仓横向对比
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundCompareTool {

    private final AnalyticsService analyticsService;
    private final FundService fundService;
    private final FundNavService fundNavService;

    /**
     * 对比多只基金的核心指标
     */
    public ToolResult<Map<String, Object>> compareFunds(List<String> fundCodes) {
        log.info("[FundCompareTool] compareFunds: fundCodes={}", fundCodes);
        try {
            if (fundCodes == null || fundCodes.size() < 2) {
                return ToolResult.fail("至少需要2只基金进行对比");
            }
            if (fundCodes.size() > 5) {
                return ToolResult.fail("最多对比5只基金");
            }

            Map<String, Object> result = new LinkedHashMap<>();

            // 1. 基本信息对比
            List<FundDetailVO> details = new ArrayList<>();
            for (String code : fundCodes) {
                try {
                    details.add(fundService.getFundDetail(code));
                } catch (Exception e) {
                    log.warn("[FundCompareTool] 基金{}查询失败: {}", code, e.getMessage());
                }
            }
            result.put("fundDetails", details);

            // 2. 业绩指标对比
            Map<String, FundPerformanceVO> performanceMap = analyticsService.compareFunds(fundCodes);
            result.put("performance", performanceMap);

            // 3. 净值走势对比（最近90天）
            Map<String, List<NavPointVO>> navTrends = new LinkedHashMap<>();
            for (String code : fundCodes) {
                try {
                    FundNavVO nav = fundNavService.getNavData(code, 90);
                    navTrends.put(code, nav.getTrend());
                } catch (Exception e) {
                    log.warn("[FundCompareTool] 基金{}净值查询失败: {}", code, e.getMessage());
                }
            }
            result.put("navTrends", navTrends);

            return ToolResult.ok("对比完成，共" + fundCodes.size() + "只基金", result);
        } catch (Exception e) {
            log.error("[FundCompareTool] compareFunds 异常", e);
            return ToolResult.fail("基金对比失败: " + e.getMessage());
        }
    }
}
