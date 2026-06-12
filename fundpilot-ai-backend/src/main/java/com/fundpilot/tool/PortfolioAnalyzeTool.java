package com.fundpilot.tool;

import com.fundpilot.service.PortfolioAnalysisService;
import com.fundpilot.service.PortfolioService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.PortfolioAnalysisVO;
import com.fundpilot.vo.PortfolioVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PortfolioAnalyzeTool — 投资组合分析
 * 职责：组合收益、风险、行业分布、重仓股重合度、风险评分
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioAnalyzeTool {

    private final PortfolioService portfolioService;
    private final PortfolioAnalysisService portfolioAnalysisService;

    /**
     * 获取用户的所有投资组合列表
     */
    public ToolResult<List<PortfolioVO>> getUserPortfolios(Long userId) {
        log.info("[PortfolioAnalyzeTool] getUserPortfolios: userId={}", userId);
        try {
            List<PortfolioVO> portfolios = portfolioService.getUserPortfolios(userId);
            return ToolResult.ok("共" + portfolios.size() + "个组合", portfolios);
        } catch (Exception e) {
            log.error("[PortfolioAnalyzeTool] getUserPortfolios 异常: userId={}", userId, e);
            return ToolResult.fail("查询组合列表失败: " + e.getMessage());
        }
    }

    /**
     * 深度分析用户的投资组合
     */
    public ToolResult<PortfolioAnalysisVO> analyzePortfolio(Long userId, Long portfolioId) {
        log.info("[PortfolioAnalyzeTool] analyzePortfolio: userId={}, portfolioId={}", userId, portfolioId);
        try {
            PortfolioAnalysisVO analysis = portfolioAnalysisService.analyze(userId, portfolioId);
            return ToolResult.ok(analysis);
        } catch (Exception e) {
            log.error("[PortfolioAnalyzeTool] analyzePortfolio 异常: userId={}, portfolioId={}",
                    userId, portfolioId, e);
            return ToolResult.fail("组合分析失败: " + e.getMessage());
        }
    }
}
