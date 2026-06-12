package com.fundpilot.service;

import com.fundpilot.vo.PortfolioAnalysisVO;

/**
 * 组合分析服务接口
 */
public interface PortfolioAnalysisService {

    /**
     * 分析用户的投资组合
     *
     * @param userId      用户ID
     * @param portfolioId 组合ID
     * @return 组合分析结果
     */
    PortfolioAnalysisVO analyze(Long userId, Long portfolioId);
}
