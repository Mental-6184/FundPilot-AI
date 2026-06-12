package com.fundpilot.tool;

import com.fundpilot.service.FundHoldingService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.FundHoldingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * FundHoldingTool — 基金持仓查询
 * 职责：前十大重仓股、持仓占比、报告日期
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundHoldingTool {

    private final FundHoldingService fundHoldingService;

    /**
     * 获取基金的持仓信息
     */
    @Cacheable(value = "fund:holding", key = "#fundCode", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<FundHoldingVO> getFundHoldings(String fundCode) {
        log.info("[FundHoldingTool] getFundHoldings: fundCode={}", fundCode);
        try {
            FundHoldingVO holdingData = fundHoldingService.getHoldingData(fundCode);
            if (holdingData.getTopHoldings() == null || holdingData.getTopHoldings().isEmpty()) {
                return ToolResult.ok("该基金暂无持仓数据", holdingData);
            }
            return ToolResult.ok(holdingData);
        } catch (Exception e) {
            log.error("[FundHoldingTool] getFundHoldings 异常: fundCode={}", fundCode, e);
            return ToolResult.fail("查询基金持仓失败: " + e.getMessage());
        }
    }
}
