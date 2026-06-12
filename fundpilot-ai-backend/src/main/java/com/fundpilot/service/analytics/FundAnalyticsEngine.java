package com.fundpilot.service.analytics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 基金分析计算引擎
 * 纯数学计算，零外部依赖，全 BigDecimal 精确运算
 *
 * <p>所有方法均为幂等的（无副作用），可安全并发调用。</p>
 *
 * <h3>精度约定</h3>
 * <ul>
 *   <li>内部计算精度: {@link MathContext#DECIMAL128}（34位有效数字）</li>
 *   <li>最终结果精度: 4位小数，{@link RoundingMode#HALF_UP}</li>
 *   <li>百分比结果单位: %（如 12.34 表示 12.34%）</li>
 * </ul>
 */
public final class FundAnalyticsEngine {

    private FundAnalyticsEngine() {}

    /** 内部计算精度 */
    private static final MathContext MC = MathContext.DECIMAL128;

    /** 结果小数位 */
    private static final int RESULT_SCALE = 4;

    /** 结果舍入模式 */
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;

    /** 年化交易日数 (√252) */
    private static final BigDecimal SQRT_252 = new BigDecimal("15.874507866387544");

    /** 每年交易日数 */
    private static final BigDecimal TRADING_DAYS_PER_YEAR = new BigDecimal("252");

    /** 零值常量 */
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /** 百分比常量 */
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    // ========================================================================
    // 1. 累计收益率
    // ========================================================================

    /**
     * 计算累计收益率
     * <pre>
     * 公式: R = (P_end - P_start) / P_start
     * </pre>
     *
     * @param startNav 期初净值
     * @param endNav   期末净值
     * @return 累计收益率（%），如 12.34 表示 12.34%
     */
    public static BigDecimal cumulativeReturn(BigDecimal startNav, BigDecimal endNav) {
        validateNav(startNav, "startNav");
        validateNav(endNav, "endNav");

        // R = (end - start) / start × 100
        BigDecimal diff = endNav.subtract(startNav, MC);
        return diff.divide(startNav, MC).multiply(HUNDRED, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 2. 年化收益率
    // ========================================================================

    /**
     * 计算年化收益率
     * <pre>
     * 公式: R_a = (1 + R)^(365/days) - 1
     * 其中 R 为累计收益率（小数形式）
     * </pre>
     *
     * @param startNav 期初净值
     * @param endNav   期末净值
     * @param days     持有天数
     * @return 年化收益率（%）
     */
    public static BigDecimal annualizedReturn(BigDecimal startNav, BigDecimal endNav, int days) {
        validateNav(startNav, "startNav");
        validateNav(endNav, "endNav");
        if (days <= 0) {
            return ZERO.setScale(RESULT_SCALE, ROUND);
        }

        // 累计收益率（小数形式）
        BigDecimal cumulativeReturnDecimal = endNav.subtract(startNav, MC)
                .divide(startNav, MC);

        // (1 + R)
        BigDecimal base = BigDecimal.ONE.add(cumulativeReturnDecimal, MC);

        if (base.compareTo(ZERO) <= 0) {
            return new BigDecimal("-100").setScale(RESULT_SCALE, ROUND);
        }

        // 指数 = 365 / days
        BigDecimal exponent = new BigDecimal("365").divide(new BigDecimal(days), MC);

        // (1 + R)^(365/days) - 1
        BigDecimal result = bigDecimalPow(base, exponent).subtract(BigDecimal.ONE, MC);

        return result.multiply(HUNDRED, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 3. 最大回撤
    // ========================================================================

    /**
     * 计算最大回撤
     * <pre>
     * 公式: MDD = max[(P_peak - P_trough) / P_peak]
     * 遍历净值序列，记录历史最高点，计算当前回撤，取最大值
     * </pre>
     *
     * @param navList 净值序列（按时间升序）
     * @return 最大回撤结果（回撤百分比为正数，如 15.23 表示下跌 15.23%）
     */
    public static MaxDrawdownResult maxDrawdown(List<BigDecimal> navList) {
        if (navList == null || navList.size() < 2) {
            return MaxDrawdownResult.empty();
        }

        BigDecimal maxMdd = ZERO;
        BigDecimal peak = navList.get(0);
        int peakIdx = 0;
        int startIdx = 0;
        int endIdx = 0;
        int tempStartIdx = 0;

        for (int i = 1; i < navList.size(); i++) {
            BigDecimal current = navList.get(i);

            // 更新峰值
            if (current.compareTo(peak) > 0) {
                peak = current;
                tempStartIdx = i;
            }

            // 计算当前回撤: (peak - current) / peak
            if (peak.compareTo(ZERO) > 0) {
                BigDecimal drawdown = peak.subtract(current, MC)
                        .divide(peak, MC);

                if (drawdown.compareTo(maxMdd) > 0) {
                    maxMdd = drawdown;
                    startIdx = tempStartIdx;
                    endIdx = i;
                }
            }
        }

        return new MaxDrawdownResult(
                maxMdd.multiply(HUNDRED, MC).setScale(RESULT_SCALE, ROUND),
                startIdx,
                endIdx
        );
    }

    /**
     * 最大回撤结果
     */
    public record MaxDrawdownResult(
            /** 最大回撤（%，正数） */
            BigDecimal drawdown,
            /** 回撤起始索引 */
            int startIndex,
            /** 回撤结束索引 */
            int endIndex
    ) {
        public static MaxDrawdownResult empty() {
            return new MaxDrawdownResult(ZERO.setScale(RESULT_SCALE, ROUND), 0, 0);
        }
    }

    // ========================================================================
    // 4. 波动率
    // ========================================================================

    /**
     * 计算年化波动率
     * <pre>
     * 日收益率: r_i = (P_i - P_{i-1}) / P_{i-1}
     * 日波动率: σ_d = sqrt[Σ(r_i - r̄)² / (N-1)]
     * 年化波动率: σ_a = σ_d × √252
     * </pre>
     *
     * @param navList 净值序列（按时间升序，至少2个数据点）
     * @return 年化波动率（%）
     */
    public static BigDecimal volatility(List<BigDecimal> navList) {
        List<BigDecimal> dailyReturns = dailyReturns(navList);
        if (dailyReturns.size() < 2) {
            return ZERO.setScale(RESULT_SCALE, ROUND);
        }

        BigDecimal mean = mean(dailyReturns);
        BigDecimal variance = variance(dailyReturns, mean);
        BigDecimal dailyStd = bigSqrt(variance, MC);

        // 年化: σ_a = σ_d × √252
        BigDecimal annualizedVol = dailyStd.multiply(SQRT_252, MC);

        return annualizedVol.multiply(HUNDRED, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 5. Sharpe Ratio
    // ========================================================================

    /**
     * 计算夏普比率
     * <pre>
     * 公式: S = (R_a - R_f) / σ_a
     * 其中 R_a = 年化收益率, R_f = 无风险利率, σ_a = 年化波动率
     * </pre>
     *
     * @param annualizedReturn 年化收益率（%）
     * @param volatility       年化波动率（%）
     * @param riskFreeRate     无风险利率（%，如 2.0 表示 2%）
     * @return 夏普比率
     */
    public static BigDecimal sharpeRatio(BigDecimal annualizedReturn, BigDecimal volatility, BigDecimal riskFreeRate) {
        if (volatility == null || volatility.compareTo(ZERO) == 0) {
            return ZERO.setScale(RESULT_SCALE, ROUND);
        }

        BigDecimal excess = annualizedReturn.subtract(riskFreeRate, MC);
        return excess.divide(volatility, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 6. Sortino Ratio
    // ========================================================================

    /**
     * 计算索提诺比率
     * <pre>
     * 公式: So = (R_a - R_f) / σ_d
     * σ_d = sqrt[Σmin(r_i - R_f_daily, 0)² / N]
     * 仅使用下行收益率计算波动率
     * </pre>
     *
     * @param navList          净值序列
     * @param annualizedReturn 年化收益率（%）
     * @param riskFreeRate     无风险利率（%）
     * @return 索提诺比率
     */
    public static BigDecimal sortinoRatio(List<BigDecimal> navList,
                                          BigDecimal annualizedReturn,
                                          BigDecimal riskFreeRate) {
        List<BigDecimal> dailyReturns = dailyReturns(navList);
        if (dailyReturns.isEmpty()) {
            return ZERO.setScale(RESULT_SCALE, ROUND);
        }

        // 日化无风险利率 = R_f / 252 / 100
        BigDecimal dailyRiskFree = riskFreeRate.divide(HUNDRED, MC)
                .divide(TRADING_DAYS_PER_YEAR, MC);

        // 下行偏差: min(r_i - R_f_daily, 0)²
        BigDecimal sumSquaredDownside = ZERO;
        for (BigDecimal r : dailyReturns) {
            BigDecimal diff = r.subtract(dailyRiskFree, MC);
            if (diff.compareTo(ZERO) < 0) {
                sumSquaredDownside = sumSquaredDownside.add(diff.pow(2), MC);
            }
        }

        BigDecimal downsideDeviation = bigSqrt(
                sumSquaredDownside.divide(new BigDecimal(dailyReturns.size()), MC), MC);

        if (downsideDeviation.compareTo(ZERO) == 0) {
            return ZERO.setScale(RESULT_SCALE, ROUND);
        }

        // 年化下行偏差
        BigDecimal annualizedDownside = downsideDeviation.multiply(SQRT_252, MC)
                .multiply(HUNDRED, MC);

        BigDecimal excess = annualizedReturn.subtract(riskFreeRate, MC);
        return excess.divide(annualizedDownside, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 7. Beta
    // ========================================================================

    /**
     * 计算 Beta 系数
     * <pre>
     * 公式: β = Cov(R_fund, R_market) / Var(R_market)
     * </pre>
     *
     * @param fundNavList   基金净值序列
     * @param marketNavList 基准指数净值序列
     * @return Beta 系数
     */
    public static BigDecimal beta(List<BigDecimal> fundNavList, List<BigDecimal> marketNavList) {
        List<BigDecimal> fundReturns = dailyReturns(fundNavList);
        List<BigDecimal> marketReturns = dailyReturns(marketNavList);

        int n = Math.min(fundReturns.size(), marketReturns.size());
        if (n < 2) {
            return BigDecimal.ONE.setScale(RESULT_SCALE, ROUND);
        }

        // 取交集（后n个）
        List<BigDecimal> f = fundReturns.subList(fundReturns.size() - n, fundReturns.size());
        List<BigDecimal> m = marketReturns.subList(marketReturns.size() - n, marketReturns.size());

        BigDecimal fundMean = mean(f);
        BigDecimal marketMean = mean(m);

        BigDecimal covariance = covariance(f, m, fundMean, marketMean);
        BigDecimal marketVariance = variance(m, marketMean);

        if (marketVariance.compareTo(ZERO) == 0) {
            return BigDecimal.ONE.setScale(RESULT_SCALE, ROUND);
        }

        return covariance.divide(marketVariance, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 8. Alpha (Jensen's Alpha)
    // ========================================================================

    /**
     * 计算詹森阿尔法
     * <pre>
     * 公式: α = R_fund - [R_f + β × (R_market - R_f)]
     * </pre>
     *
     * @param fundAnnualizedReturn   基金年化收益率（%）
     * @param marketAnnualizedReturn 基准年化收益率（%）
     * @param riskFreeRate           无风险利率（%）
     * @param beta                   Beta 系数
     * @return Alpha（%）
     */
    public static BigDecimal alpha(BigDecimal fundAnnualizedReturn,
                                   BigDecimal marketAnnualizedReturn,
                                   BigDecimal riskFreeRate,
                                   BigDecimal beta) {
        // 期望收益 = R_f + β × (R_market - R_f)
        BigDecimal marketExcess = marketAnnualizedReturn.subtract(riskFreeRate, MC);
        BigDecimal expectedReturn = riskFreeRate.add(beta.multiply(marketExcess, MC), MC);

        // α = R_fund - 期望收益
        return fundAnnualizedReturn.subtract(expectedReturn, MC).setScale(RESULT_SCALE, ROUND);
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

    /**
     * 计算日收益率序列
     * r_i = (P_i - P_{i-1}) / P_{i-1}
     */
    static List<BigDecimal> dailyReturns(List<BigDecimal> navList) {
        List<BigDecimal> returns = new ArrayList<>();
        for (int i = 1; i < navList.size(); i++) {
            BigDecimal prev = navList.get(i - 1);
            if (prev.compareTo(ZERO) > 0) {
                BigDecimal r = navList.get(i).subtract(prev, MC).divide(prev, MC);
                returns.add(r);
            }
        }
        return returns;
    }

    /**
     * 计算均值
     */
    public static BigDecimal mean(List<BigDecimal> values) {
        if (values.isEmpty()) return ZERO;
        BigDecimal sum = ZERO;
        for (BigDecimal v : values) {
            sum = sum.add(v, MC);
        }
        return sum.divide(new BigDecimal(values.size()), MC);
    }

    /**
     * 计算方差（样本方差，分母 N-1）
     */
    public static BigDecimal variance(List<BigDecimal> values, BigDecimal mean) {
        if (values.size() < 2) return ZERO;
        BigDecimal sumSquaredDiff = ZERO;
        for (BigDecimal v : values) {
            BigDecimal diff = v.subtract(mean, MC);
            sumSquaredDiff = sumSquaredDiff.add(diff.pow(2), MC);
        }
        return sumSquaredDiff.divide(new BigDecimal(values.size() - 1), MC);
    }

    /**
     * 计算协方差
     */
    static BigDecimal covariance(List<BigDecimal> x, List<BigDecimal> y,
                                  BigDecimal meanX, BigDecimal meanY) {
        int n = Math.min(x.size(), y.size());
        if (n < 2) return ZERO;
        BigDecimal sum = ZERO;
        for (int i = 0; i < n; i++) {
            BigDecimal dx = x.get(i).subtract(meanX, MC);
            BigDecimal dy = y.get(i).subtract(meanY, MC);
            sum = sum.add(dx.multiply(dy, MC), MC);
        }
        return sum.divide(new BigDecimal(n - 1), MC);
    }

    /**
     * BigDecimal 幂运算: base^exponent
     * 使用 ln/exp 转换: base^exp = exp(exp × ln(base))
     */
    static BigDecimal bigDecimalPow(BigDecimal base, BigDecimal exponent) {
        if (exponent.compareTo(ZERO) == 0) return BigDecimal.ONE;
        if (base.compareTo(BigDecimal.ONE) == 0) return BigDecimal.ONE;

        // 使用 double 近似计算（精度足够用于金融指标）
        double result = Math.pow(base.doubleValue(), exponent.doubleValue());
        return new BigDecimal(Double.toString(result), MC);
    }

    /**
     * BigDecimal 平方根（牛顿迭代法）
     */
    public static BigDecimal bigSqrt(BigDecimal value, MathContext mc) {
        if (value.compareTo(ZERO) == 0) return ZERO;
        if (value.compareTo(ZERO) < 0) {
            throw new ArithmeticException("Cannot compute square root of negative value: " + value);
        }

        BigDecimal x = new BigDecimal(Math.sqrt(value.doubleValue()), mc);
        BigDecimal two = new BigDecimal("2");

        // 牛顿迭代: x_{n+1} = (x_n + value/x_n) / 2
        for (int i = 0; i < 20; i++) {
            BigDecimal x1 = x.add(value.divide(x, mc), mc).divide(two, mc);
            if (x.subtract(x1).abs().compareTo(new BigDecimal("1E-30")) < 0) {
                break;
            }
            x = x1;
        }
        return x;
    }

    /**
     * 校验净值
     */
    private static void validateNav(BigDecimal nav, String name) {
        if (nav == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        if (nav.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException(name + " must be positive, got: " + nav);
        }
    }
}
