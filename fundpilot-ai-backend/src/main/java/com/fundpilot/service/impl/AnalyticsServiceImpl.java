package com.fundpilot.service.impl;

import com.fundpilot.dto.AnalyticsRequestDTO;
import com.fundpilot.entity.FundNav;
import com.fundpilot.service.AnalyticsService;
import com.fundpilot.service.FundNavService;
import com.fundpilot.service.analytics.FundAnalyticsEngine;
import com.fundpilot.service.analytics.FundAnalyticsEngine.MaxDrawdownResult;
import com.fundpilot.vo.FundPerformanceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 基金分析计算服务实现
 * 委托 FundAnalyticsEngine 完成纯数学计算
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final FundNavService fundNavService;

    private static final BigDecimal DEFAULT_RISK_FREE_RATE = new BigDecimal("2.0000");
    private static final int RESULT_SCALE = 4;

    @Override
    public FundPerformanceVO analyze(AnalyticsRequestDTO request) {
        String fundCode = request.getFundCode();
        BigDecimal riskFreeRate = request.getRiskFreeRate() != null
                ? request.getRiskFreeRate() : DEFAULT_RISK_FREE_RATE;

        FundNav latestNav = fundNavService.getLatestNav(fundCode);
        if (latestNav == null) {
            FundPerformanceVO empty = new FundPerformanceVO();
            empty.setFundCode(fundCode);
            return empty;
        }

        LocalDate endDate = latestNav.getNavDate();

        // 获取完整净值序列
        LocalDate earliestDate = fundNavService.getEarliestDate(fundCode);
        List<BigDecimal> fullNavList = getNavValues(fundCode,
                earliestDate != null ? earliestDate : endDate.minusYears(3), endDate);

        FundPerformanceVO vo = new FundPerformanceVO();
        vo.setFundCode(fundCode);

        // 计算各周期收益率
        calculatePeriodReturns(vo, fundCode, endDate, latestNav.getUnitNav());

        // 成立以来收益率 + 年化收益率
        if (earliestDate != null && !fullNavList.isEmpty()) {
            BigDecimal startNav = fullNavList.get(0);
            BigDecimal endNav = fullNavList.get(fullNavList.size() - 1);
            int totalDays = (int) ChronoUnit.DAYS.between(earliestDate, endDate);

            vo.setReturnSinceEstablish(FundAnalyticsEngine.cumulativeReturn(startNav, endNav));
            vo.setAnnualizedReturn(FundAnalyticsEngine.annualizedReturn(startNav, endNav, totalDays));
        }

        // 风险指标（需要至少20个数据点）
        if (fullNavList.size() >= 20) {
            calculateRiskMetrics(vo, fullNavList, riskFreeRate);
        }

        // Alpha / Beta（需要基准数据）
        if (request.getBenchmarkCode() != null && !request.getBenchmarkCode().isEmpty()) {
            calculateAlphaBeta(vo, fundCode, request.getBenchmarkCode(), riskFreeRate, endDate);
        }

        return vo;
    }

    @Override
    public FundPerformanceVO calculateFullPerformance(String fundCode) {
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setFundCode(fundCode);
        request.setPeriod("all");
        return analyze(request);
    }

    @Override
    public Map<String, FundPerformanceVO> compareFunds(List<String> fundCodes) {
        Map<String, FundPerformanceVO> result = new LinkedHashMap<>();
        for (String code : fundCodes) {
            result.put(code, calculateFullPerformance(code));
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 计算各周期收益率
     */
    private void calculatePeriodReturns(FundPerformanceVO vo, String fundCode,
                                         LocalDate endDate, BigDecimal endNav) {
        vo.setReturn1w(calculateReturn(fundCode, endDate, endNav, 7));
        vo.setReturn1m(calculateReturn(fundCode, endDate, endNav, 30));
        vo.setReturn3m(calculateReturn(fundCode, endDate, endNav, 91));
        vo.setReturn6m(calculateReturn(fundCode, endDate, endNav, 182));
        vo.setReturn1y(calculateReturn(fundCode, endDate, endNav, 365));
        vo.setReturn3y(calculateReturn(fundCode, endDate, endNav, 1095));
    }

    /**
     * 计算区间收益率
     */
    private BigDecimal calculateReturn(String fundCode, LocalDate endDate,
                                        BigDecimal endNav, int days) {
        LocalDate startDate = endDate.minusDays(days);
        FundNav startNav = fundNavService.getNavByDate(fundCode, startDate);
        if (startNav == null) {
            startNav = fundNavService.getNavBeforeDate(fundCode, startDate);
        }
        if (startNav == null) return null;
        return FundAnalyticsEngine.cumulativeReturn(startNav.getUnitNav(), endNav);
    }

    /**
     * 计算风险指标
     */
    private void calculateRiskMetrics(FundPerformanceVO vo, List<BigDecimal> navList,
                                       BigDecimal riskFreeRate) {
        // 波动率
        BigDecimal vol = FundAnalyticsEngine.volatility(navList);
        vo.setAnnualizedVolatility(vol);

        // 最大回撤
        MaxDrawdownResult mdd = FundAnalyticsEngine.maxDrawdown(navList);
        vo.setMaxDrawdown(mdd.drawdown());
        vo.setMaxDrawdownStart(mdd.startIndex());
        vo.setMaxDrawdownEnd(mdd.endIndex());

        // 夏普比率
        if (vo.getAnnualizedReturn() != null) {
            vo.setSharpeRatio(FundAnalyticsEngine.sharpeRatio(
                    vo.getAnnualizedReturn(), vol, riskFreeRate));
        }

        // 索提诺比率
        if (vo.getAnnualizedReturn() != null) {
            vo.setSortinoRatio(FundAnalyticsEngine.sortinoRatio(
                    navList, vo.getAnnualizedReturn(), riskFreeRate));
        }

        // 卡尔马比率 = 年化收益率 / 最大回撤
        if (vo.getAnnualizedReturn() != null
                && mdd.drawdown().compareTo(BigDecimal.ZERO) > 0) {
            vo.setCalmarRatio(vo.getAnnualizedReturn()
                    .divide(mdd.drawdown(), 4, RoundingMode.HALF_UP));
        }
    }

    /**
     * 计算 Alpha / Beta
     */
    private void calculateAlphaBeta(FundPerformanceVO vo, String fundCode,
                                     String benchmarkCode, BigDecimal riskFreeRate,
                                     LocalDate endDate) {
        vo.setBenchmarkCode(benchmarkCode);

        LocalDate startDate = endDate.minusYears(1);
        List<BigDecimal> fundNavList = getNavValues(fundCode, startDate, endDate);
        List<BigDecimal> marketNavList = getNavValues(benchmarkCode, startDate, endDate);

        if (fundNavList.size() < 20 || marketNavList.size() < 20) {
            return;
        }

        // Beta
        BigDecimal betaValue = FundAnalyticsEngine.beta(fundNavList, marketNavList);
        vo.setBeta(betaValue);

        // Alpha
        if (vo.getAnnualizedReturn() != null) {
            BigDecimal startNav = marketNavList.get(0);
            BigDecimal endNav = marketNavList.get(marketNavList.size() - 1);
            int days = (int) ChronoUnit.DAYS.between(startDate, endDate);
            BigDecimal marketAnnualized = FundAnalyticsEngine.annualizedReturn(startNav, endNav, days);

            vo.setAlpha(FundAnalyticsEngine.alpha(
                    vo.getAnnualizedReturn(), marketAnnualized, riskFreeRate, betaValue));
        }
    }

    /**
     * 获取净值列表（BigDecimal）
     */
    private List<BigDecimal> getNavValues(String fundCode, LocalDate startDate, LocalDate endDate) {
        List<FundNav> navs = fundNavService.getNavBetween(fundCode, startDate, endDate);
        List<BigDecimal> values = new ArrayList<>(navs.size());
        for (FundNav nav : navs) {
            values.add(nav.getUnitNav());
        }
        return values;
    }
}
