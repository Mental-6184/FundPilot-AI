package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fundpilot.common.enums.RiskLevel;
import com.fundpilot.entity.Fund;
import com.fundpilot.entity.FundHolding;
import com.fundpilot.entity.FundNav;
import com.fundpilot.entity.PortfolioItem;
import com.fundpilot.mapper.FundHoldingMapper;
import com.fundpilot.mapper.PortfolioItemMapper;
import com.fundpilot.service.FundNavService;
import com.fundpilot.service.FundService;
import com.fundpilot.service.PortfolioAnalysisService;
import com.fundpilot.service.PortfolioService;
import com.fundpilot.service.analytics.FundAnalyticsEngine;
import com.fundpilot.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组合分析服务实现
 * 核心：基于基金净值的加权组合分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioAnalysisServiceImpl implements PortfolioAnalysisService {

    private final PortfolioService portfolioService;
    private final PortfolioItemMapper portfolioItemMapper;
    private final FundService fundService;
    private final FundNavService fundNavService;
    private final FundHoldingMapper fundHoldingMapper;

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final int SCALE = 4;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Override
    public PortfolioAnalysisVO analyze(Long userId, Long portfolioId) {
        // 1. 获取组合基础信息
        PortfolioVO portfolio = portfolioService.getPortfolioDetail(userId, portfolioId);
        List<PortfolioItem> items = getPortfolioItems(portfolioId);

        PortfolioAnalysisVO vo = new PortfolioAnalysisVO();
        vo.setPortfolioId(portfolioId);
        vo.setPortfolioName(portfolio.getPortfolioName());
        vo.setFundItems(portfolio.getFunds());

        if (items.isEmpty()) {
            return vo;
        }

        // 2. 获取基金代码和权重
        List<String> fundCodes = items.stream().map(PortfolioItem::getFundCode).toList();
        Map<String, BigDecimal> weightMap = calculateWeights(items);

        // 3. 计算组合收益指标
        calculateReturns(vo, items, fundCodes, weightMap);

        // 4. 计算组合风险指标
        calculateRiskMetrics(vo, fundCodes, weightMap);

        // 5. 风险评分
        calculateRiskScore(vo, fundCodes, items);

        // 6. 行业分布
        vo.setIndustryDistribution(analyzeIndustryDistribution(fundCodes, weightMap));

        // 7. 重仓股重合度
        analyzeHoldingOverlap(vo, fundCodes);

        return vo;
    }

    // ==================== 收益计算 ====================

    private void calculateReturns(PortfolioAnalysisVO vo, List<PortfolioItem> items,
                                   List<String> fundCodes, Map<String, BigDecimal> weightMap) {
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal weightedReturn = BigDecimal.ZERO;

        for (PortfolioItem item : items) {
            FundNav nav = fundNavService.getLatestNav(item.getFundCode());
            if (nav == null || item.getBuyPrice() == null) continue;

            // 单基金盈亏
            if (item.getInvestShares() != null) {
                BigDecimal currentValue = nav.getUnitNav().multiply(item.getInvestShares(), MC);
                BigDecimal costValue = item.getBuyPrice().multiply(item.getInvestShares(), MC);
                totalProfit = totalProfit.add(currentValue.subtract(costValue, MC), MC);
            }

            // 单基金收益率
            BigDecimal fundReturn = nav.getUnitNav().subtract(item.getBuyPrice(), MC)
                    .divide(item.getBuyPrice(), MC);
            BigDecimal weight = weightMap.getOrDefault(item.getFundCode(), BigDecimal.ZERO);
            weightedReturn = weightedReturn.add(fundReturn.multiply(weight, MC), MC);
        }

        vo.setTotalProfit(totalProfit.setScale(2, ROUND));
        vo.setCumulativeReturn(weightedReturn.multiply(HUNDRED, MC).setScale(SCALE, ROUND));

        // 年化收益率（假设组合成立1年，简化计算）
        vo.setAnnualizedReturn(vo.getCumulativeReturn());
    }

    // ==================== 风险指标计算 ====================

    private void calculateRiskMetrics(PortfolioAnalysisVO vo, List<String> fundCodes,
                                       Map<String, BigDecimal> weightMap) {
        // 获取所有基金的净值序列（取最近1年）
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);

        Map<String, List<BigDecimal>> navMap = new LinkedHashMap<>();
        for (String code : fundCodes) {
            List<FundNav> navs = fundNavService.getNavBetween(code, startDate, endDate);
            if (navs.size() >= 20) {
                navMap.put(code, navs.stream().map(FundNav::getUnitNav).toList());
            }
        }

        if (navMap.isEmpty()) return;

        // 计算组合加权日收益率序列
        List<BigDecimal> portfolioReturns = calculatePortfolioReturns(navMap, weightMap);
        if (portfolioReturns.size() < 20) return;

        // 组合波动率
        BigDecimal mean = FundAnalyticsEngine.mean(portfolioReturns);
        BigDecimal variance = FundAnalyticsEngine.variance(portfolioReturns, mean);
        BigDecimal dailyStd = FundAnalyticsEngine.bigSqrt(variance, MC);
        BigDecimal vol = dailyStd.multiply(new BigDecimal("15.874507866387544"), MC)
                .multiply(HUNDRED, MC);
        vo.setVolatility(vol.setScale(SCALE, ROUND));

        // 组合净值序列（用于最大回撤）
        List<BigDecimal> portfolioNav = buildPortfolioNav(navMap, weightMap);
        FundAnalyticsEngine.MaxDrawdownResult mdd = FundAnalyticsEngine.maxDrawdown(portfolioNav);
        vo.setMaxDrawdown(mdd.drawdown());

        // 夏普比率
        if (vo.getAnnualizedReturn() != null) {
            BigDecimal riskFreeRate = new BigDecimal("2.0000");
            vo.setSharpeRatio(FundAnalyticsEngine.sharpeRatio(vo.getAnnualizedReturn(), vol, riskFreeRate));
        }
    }

    /**
     * 计算组合加权日收益率序列
     */
    private List<BigDecimal> calculatePortfolioReturns(Map<String, List<BigDecimal>> navMap,
                                                        Map<String, BigDecimal> weightMap) {
        // 取最短序列长度
        int minLen = navMap.values().stream().mapToInt(List::size).min().orElse(0);
        if (minLen < 2) return List.of();

        // 归一化权重
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (String code : navMap.keySet()) {
            totalWeight = totalWeight.add(weightMap.getOrDefault(code, BigDecimal.ZERO), MC);
        }
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) return List.of();

        List<BigDecimal> portfolioReturns = new ArrayList<>();
        for (int i = 1; i < minLen; i++) {
            BigDecimal weightedReturn = BigDecimal.ZERO;
            for (var entry : navMap.entrySet()) {
                List<BigDecimal> navs = entry.getValue();
                BigDecimal weight = weightMap.getOrDefault(entry.getKey(), BigDecimal.ZERO)
                        .divide(totalWeight, MC);
                BigDecimal dailyR = navs.get(i).subtract(navs.get(i - 1), MC)
                        .divide(navs.get(i - 1), MC);
                weightedReturn = weightedReturn.add(dailyR.multiply(weight, MC), MC);
            }
            portfolioReturns.add(weightedReturn);
        }
        return portfolioReturns;
    }

    /**
     * 构建组合净值序列（加权）
     */
    private List<BigDecimal> buildPortfolioNav(Map<String, List<BigDecimal>> navMap,
                                                Map<String, BigDecimal> weightMap) {
        int minLen = navMap.values().stream().mapToInt(List::size).min().orElse(0);
        if (minLen == 0) return List.of();

        BigDecimal totalWeight = BigDecimal.ZERO;
        for (String code : navMap.keySet()) {
            totalWeight = totalWeight.add(weightMap.getOrDefault(code, BigDecimal.ZERO), MC);
        }

        List<BigDecimal> result = new ArrayList<>();
        for (int i = 0; i < minLen; i++) {
            BigDecimal weightedNav = BigDecimal.ZERO;
            for (var entry : navMap.entrySet()) {
                BigDecimal weight = weightMap.getOrDefault(entry.getKey(), BigDecimal.ZERO)
                        .divide(totalWeight, MC);
                weightedNav = weightedNav.add(entry.getValue().get(i).multiply(weight, MC), MC);
            }
            result.add(weightedNav);
        }
        return result;
    }

    // ==================== 风险评分 ====================

    private void calculateRiskScore(PortfolioAnalysisVO vo, List<String> fundCodes,
                                     List<PortfolioItem> items) {
        RiskScoreDetailVO detail = new RiskScoreDetailVO();

        // 权重配置
        BigDecimal wVol = new BigDecimal("0.30");
        BigDecimal wMdd = new BigDecimal("0.30");
        BigDecimal wCon = new BigDecimal("0.20");
        BigDecimal wFund = new BigDecimal("0.20");
        detail.setVolatilityWeight(wVol);
        detail.setDrawdownWeight(wMdd);
        detail.setConcentrationWeight(wCon);
        detail.setFundRiskWeight(wFund);

        // 1. 波动率得分 (波动率越高，得分越高)
        int volScore = scoreVolatility(vo.getVolatility());
        detail.setVolatilityScore(volScore);

        // 2. 最大回撤得分
        int mddScore = scoreDrawdown(vo.getMaxDrawdown());
        detail.setDrawdownScore(mddScore);

        // 3. 集中度得分 (基金越少，集中度越高)
        int conScore = scoreConcentration(items.size());
        detail.setConcentrationScore(conScore);

        // 4. 基金风险等级得分
        int fundRiskScore = scoreFundRisk(fundCodes);
        detail.setFundRiskScore(fundRiskScore);

        // 综合得分 = 加权平均
        int totalScore = (int) Math.round(
                volScore * wVol.doubleValue() +
                mddScore * wMdd.doubleValue() +
                conScore * wCon.doubleValue() +
                fundRiskScore * wFund.doubleValue()
        );
        totalScore = Math.max(1, Math.min(100, totalScore));

        vo.setRiskScore(totalScore);
        vo.setRiskScoreDetail(detail);

        // 映射风险等级
        vo.setRiskLevel(scoreToLevel(totalScore));
        vo.setRiskLevelName(RiskLevel.fromCode(vo.getRiskLevel()).getDesc());
    }

    private int scoreVolatility(BigDecimal vol) {
        if (vol == null) return 50;
        double v = vol.doubleValue();
        if (v < 5) return 10;
        if (v < 10) return 20;
        if (v < 15) return 35;
        if (v < 20) return 50;
        if (v < 30) return 65;
        if (v < 40) return 80;
        return 95;
    }

    private int scoreDrawdown(BigDecimal mdd) {
        if (mdd == null) return 50;
        double d = mdd.doubleValue();
        if (d < 3) return 10;
        if (d < 5) return 20;
        if (d < 10) return 35;
        if (d < 15) return 50;
        if (d < 25) return 65;
        if (d < 40) return 80;
        return 95;
    }

    private int scoreConcentration(int fundCount) {
        if (fundCount >= 10) return 10;
        if (fundCount >= 7) return 25;
        if (fundCount >= 5) return 40;
        if (fundCount >= 3) return 60;
        return 85;
    }

    private int scoreFundRisk(List<String> fundCodes) {
        Map<String, Integer> riskMap = Map.of(
                "LOW", 15, "MEDIUM_LOW", 30, "MEDIUM", 50,
                "MEDIUM_HIGH", 70, "HIGH", 90
        );
        int total = 0;
        int count = 0;
        for (String code : fundCodes) {
            Fund fund = fundService.getByFundCode(code);
            if (fund != null && fund.getRiskLevel() != null) {
                total += riskMap.getOrDefault(fund.getRiskLevel(), 50);
                count++;
            }
        }
        return count > 0 ? total / count : 50;
    }

    private String scoreToLevel(int score) {
        if (score <= 20) return "LOW";
        if (score <= 40) return "MEDIUM_LOW";
        if (score <= 60) return "MEDIUM";
        if (score <= 80) return "MEDIUM_HIGH";
        return "HIGH";
    }

    // ==================== 行业分布 ====================

    private List<IndustryDistributionVO> analyzeIndustryDistribution(List<String> fundCodes,
                                                                       Map<String, BigDecimal> weightMap) {
        // 按基金类型聚合（简化版：用基金类型代替行业）
        Map<String, BigDecimal> typeDistribution = new LinkedHashMap<>();
        Map<String, Integer> typeCount = new LinkedHashMap<>();

        for (String code : fundCodes) {
            Fund fund = fundService.getByFundCode(code);
            if (fund == null) continue;
            String type = fund.getFundType() != null ? fund.getFundType() : "OTHER";
            BigDecimal weight = weightMap.getOrDefault(code, BigDecimal.ZERO);
            typeDistribution.merge(type, weight, BigDecimal::add);
            typeCount.merge(type, 1, Integer::sum);
        }

        List<IndustryDistributionVO> result = new ArrayList<>();
        for (var entry : typeDistribution.entrySet()) {
            IndustryDistributionVO vo = new IndustryDistributionVO();
            vo.setIndustry(entry.getKey());
            vo.setRatio(entry.getValue().multiply(HUNDRED, MC).setScale(SCALE, ROUND));
            vo.setFundCount(typeCount.getOrDefault(entry.getKey(), 0));
            result.add(vo);
        }
        result.sort((a, b) -> b.getRatio().compareTo(a.getRatio()));
        return result;
    }

    // ==================== 重仓股重合度 ====================

    private void analyzeHoldingOverlap(PortfolioAnalysisVO vo, List<String> fundCodes) {
        if (fundCodes.size() < 2) {
            vo.setHoldingOverlaps(List.of());
            vo.setOverlapScore(0);
            return;
        }

        // 获取每只基金的最新持仓
        Map<String, List<FundHolding>> holdingsMap = new LinkedHashMap<>();
        for (String code : fundCodes) {
            LocalDate reportDate = fundHoldingMapper.selectLatestReportDate(code);
            if (reportDate != null) {
                List<FundHolding> holdings = fundHoldingMapper.selectList(
                        new LambdaQueryWrapper<FundHolding>()
                                .eq(FundHolding::getFundCode, code)
                                .eq(FundHolding::getReportDate, reportDate)
                                .orderByAsc(FundHolding::getRank)
                                .last("LIMIT 10"));
                holdingsMap.put(code, holdings);
            }
        }

        // 统计每只股票出现在几只基金中
        Map<String, HoldingOverlapVO> stockMap = new LinkedHashMap<>();
        for (var entry : holdingsMap.entrySet()) {
            String fundCode = entry.getKey();
            for (FundHolding h : entry.getValue()) {
                stockMap.computeIfAbsent(h.getStockCode(), k -> {
                    HoldingOverlapVO o = new HoldingOverlapVO();
                    o.setStockCode(h.getStockCode());
                    o.setStockName(h.getStockName());
                    o.setAppearCount(0);
                    o.setFundCodes(new ArrayList<>());
                    o.setTotalRatio(BigDecimal.ZERO);
                    return o;
                });
                HoldingOverlapVO overlap = stockMap.get(h.getStockCode());
                overlap.setAppearCount(overlap.getAppearCount() + 1);
                overlap.getFundCodes().add(fundCode);
                overlap.setTotalRatio(overlap.getTotalRatio().add(
                        h.getHoldRatio() != null ? h.getHoldRatio() : BigDecimal.ZERO, MC));
            }
        }

        // 过滤出现在2只及以上基金中的股票
        List<HoldingOverlapVO> overlaps = stockMap.values().stream()
                .filter(o -> o.getAppearCount() >= 2)
                .sorted((a, b) -> b.getAppearCount() - a.getAppearCount())
                .toList();

        vo.setHoldingOverlaps(overlaps);

        // 重合度评分 = 重合股票数 / 总重仓股数 × 100
        int totalStocks = stockMap.size();
        int overlapStocks = overlaps.size();
        vo.setOverlapScore(totalStocks > 0 ? overlapStocks * 100 / totalStocks : 0);
    }

    // ==================== 辅助方法 ====================

    private List<PortfolioItem> getPortfolioItems(Long portfolioId) {
        return portfolioItemMapper.selectList(
                new LambdaQueryWrapper<PortfolioItem>()
                        .eq(PortfolioItem::getPortfolioId, portfolioId));
    }

    /**
     * 计算每只基金的权重（按投入金额）
     */
    private Map<String, BigDecimal> calculateWeights(List<PortfolioItem> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PortfolioItem item : items) {
            if (item.getInvestAmount() != null) {
                totalAmount = totalAmount.add(item.getInvestAmount(), MC);
            }
        }

        Map<String, BigDecimal> weightMap = new LinkedHashMap<>();
        for (PortfolioItem item : items) {
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0 && item.getInvestAmount() != null) {
                weightMap.put(item.getFundCode(),
                        item.getInvestAmount().divide(totalAmount, MC));
            } else {
                // 等权
                weightMap.put(item.getFundCode(),
                        BigDecimal.ONE.divide(new BigDecimal(items.size()), MC));
            }
        }
        return weightMap;
    }
}
