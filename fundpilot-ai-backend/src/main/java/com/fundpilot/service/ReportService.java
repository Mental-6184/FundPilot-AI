package com.fundpilot.service;

/**
 * AI 分析报告服务接口
 * 封装4种报告类型的生成逻辑
 */
public interface ReportService {

    /**
     * 生成基金分析报告
     *
     * @param fundCode 基金代码
     * @return Markdown 格式报告
     */
    String generateFundAnalysis(String fundCode);

    /**
     * 生成基金对比报告
     *
     * @param fundCodes 基金代码列表（逗号分隔）
     * @return Markdown 格式报告
     */
    String generateFundComparison(String fundCodes);

    /**
     * 生成组合诊断报告
     *
     * @param userId      用户ID
     * @param portfolioId 组合ID
     * @return Markdown 格式报告
     */
    String generatePortfolioDiagnosis(Long userId, Long portfolioId);

    /**
     * 生成基金推荐报告
     *
     * @param userDemand 用户需求描述
     * @param fundType   偏好类型（可为null）
     * @param riskLevel  偏好风险（可为null）
     * @param preference 排序偏好（RETURN/RISK/BALANCED）
     * @return Markdown 格式报告
     */
    String generateFundRecommendation(String userDemand, String fundType,
                                       String riskLevel, String preference);
}
