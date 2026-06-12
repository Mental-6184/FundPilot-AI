package com.fundpilot.service;

import com.fundpilot.dto.AnalyticsRequestDTO;
import com.fundpilot.vo.FundPerformanceVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 基金分析计算服务接口
 * 核心：所有指标由 Java 精确计算，非 LLM 编造
 */
public interface AnalyticsService {

    /**
     * 根据请求计算完整的业绩指标
     *
     * @param request 分析请求（含基金代码、周期、无风险利率、基准代码）
     * @return 业绩指标
     */
    FundPerformanceVO analyze(AnalyticsRequestDTO request);

    /**
     * 计算基金完整业绩指标（所有周期 + 全部风险指标）
     */
    FundPerformanceVO calculateFullPerformance(String fundCode);

    /**
     * 对比多只基金的业绩指标
     */
    Map<String, FundPerformanceVO> compareFunds(List<String> fundCodes);
}
