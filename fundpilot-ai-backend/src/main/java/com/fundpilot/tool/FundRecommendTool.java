package com.fundpilot.tool;

import com.fundpilot.common.result.PageResult;
import com.fundpilot.dto.FundQueryDTO;
import com.fundpilot.service.FundService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.FundListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * FundRecommendTool — 基金推荐
 * 职责：根据用户偏好筛选并推荐基金
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundRecommendTool {

    private final FundService fundService;

    /**
     * 根据用户偏好推荐基金
     */
    public ToolResult<List<FundListVO>> recommendFunds(String fundType, String riskLevel,
                                                        String preference, int limit) {
        log.info("[FundRecommendTool] recommendFunds: fundType={}, riskLevel={}, preference={}, limit={}",
                fundType, riskLevel, preference, limit);
        try {
            FundQueryDTO queryDTO = new FundQueryDTO();
            queryDTO.setFundType(fundType);
            queryDTO.setRiskLevel(riskLevel);
            queryDTO.setPageSize(limit > 0 ? limit : 5);

            PageResult<FundListVO> result = fundService.searchFunds(queryDTO);
            List<FundListVO> funds = result.getRecords();

            if (funds.isEmpty()) {
                return ToolResult.ok("未找到符合条件的基金", funds);
            }

            // 按偏好排序
            String pref = preference != null ? preference : "BALANCED";
            switch (pref) {
                case "RETURN" -> funds.sort(this::compareReturn);
                case "RISK" -> funds.sort(this::compareRisk);
                default -> funds.sort(this::compareBalanced);
            }

            return ToolResult.ok("推荐" + funds.size() + "只基金", funds);
        } catch (Exception e) {
            log.error("[FundRecommendTool] recommendFunds 异常", e);
            return ToolResult.fail("基金推荐失败: " + e.getMessage());
        }
    }

    private int compareReturn(FundListVO a, FundListVO b) {
        BigDecimal ra = a.getDailyReturn() != null ? a.getDailyReturn() : BigDecimal.ZERO;
        BigDecimal rb = b.getDailyReturn() != null ? b.getDailyReturn() : BigDecimal.ZERO;
        return rb.compareTo(ra);
    }

    private int compareRisk(FundListVO a, FundListVO b) {
        int riskA = riskToLevel(a.getRiskLevel());
        int riskB = riskToLevel(b.getRiskLevel());
        return Integer.compare(riskA, riskB);
    }

    private int compareBalanced(FundListVO a, FundListVO b) {
        double scoreA = balancedScore(a);
        double scoreB = balancedScore(b);
        return Double.compare(scoreB, scoreA);
    }

    private double balancedScore(FundListVO fund) {
        double returnScore = fund.getDailyReturn() != null ? fund.getDailyReturn().doubleValue() : 0;
        double riskPenalty = riskToLevel(fund.getRiskLevel()) * 0.3;
        return returnScore - riskPenalty;
    }

    private int riskToLevel(String riskLevel) {
        if (riskLevel == null) return 3;
        return switch (riskLevel) {
            case "LOW" -> 1;
            case "MEDIUM_LOW" -> 2;
            case "MEDIUM" -> 3;
            case "MEDIUM_HIGH" -> 4;
            case "HIGH" -> 5;
            default -> 3;
        };
    }
}
