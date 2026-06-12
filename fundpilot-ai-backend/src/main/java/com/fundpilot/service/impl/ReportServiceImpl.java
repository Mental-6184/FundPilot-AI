package com.fundpilot.service.impl;

import com.fundpilot.agent.report.ReportGenerator;
import com.fundpilot.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 分析报告服务实现
 * 委托 ReportGenerator 调用 LLM 生成报告
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportGenerator reportGenerator;

    @Override
    public String generateFundAnalysis(String fundCode) {
        log.info("[ReportService] 生成基金分析报告: fundCode={}", fundCode);
        return reportGenerator.generateFundAnalysis(fundCode);
    }

    @Override
    public String generateFundComparison(String fundCodes) {
        log.info("[ReportService] 生成基金对比报告: fundCodes={}", fundCodes);
        List<String> codeList = Arrays.stream(fundCodes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (codeList.size() < 2) {
            throw new IllegalArgumentException("至少需要2只基金才能进行对比");
        }
        return reportGenerator.generateFundComparison(codeList);
    }

    @Override
    public String generatePortfolioDiagnosis(Long userId, Long portfolioId) {
        log.info("[ReportService] 生成组合诊断报告: userId={}, portfolioId={}", userId, portfolioId);
        return reportGenerator.generatePortfolioDiagnosis(userId, portfolioId);
    }

    @Override
    public String generateFundRecommendation(String userDemand, String fundType,
                                              String riskLevel, String preference) {
        log.info("[ReportService] 生成基金推荐报告: demand={}", userDemand);
        return reportGenerator.generateFundRecommendation(userDemand, fundType, riskLevel, preference);
    }
}
