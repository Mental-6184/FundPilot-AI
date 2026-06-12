package com.fundpilot.service.analytics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FundAnalyticsEngine 单元测试
 * 每个指标用手工计算的已知结果验证
 */
@DisplayName("基金分析计算引擎测试")
class FundAnalyticsEngineTest {

    private static final int SCALE = 4;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;

    /** 辅助: 从 double 创建 BigDecimal */
    private static BigDecimal bd(double val) {
        return BigDecimal.valueOf(val);
    }

    /** 辅助: 从 double 列表创建 BigDecimal 列表 */
    private static List<BigDecimal> bdList(double... vals) {
        return Arrays.stream(vals)
                .mapToObj(BigDecimal::valueOf)
                .toList();
    }

    // ========================================================================
    // 1. 累计收益率测试
    // ========================================================================
    @Nested
    @DisplayName("累计收益率 (Cumulative Return)")
    class CumulativeReturnTest {

        @Test
        @DisplayName("正常计算: 1.0 → 1.2，收益率 20%")
        void normal_case() {
            BigDecimal result = FundAnalyticsEngine.cumulativeReturn(bd(1.0), bd(1.2));
            assertEquals(bd(20.0000), result);
        }

        @Test
        @DisplayName("下跌: 1.0 → 0.8，收益率 -20%")
        void negative_return() {
            BigDecimal result = FundAnalyticsEngine.cumulativeReturn(bd(1.0), bd(0.8));
            assertEquals(bd(-20.0000), result);
        }

        @Test
        @DisplayName("零收益: 1.0 → 1.0，收益率 0%")
        void zero_return() {
            BigDecimal result = FundAnalyticsEngine.cumulativeReturn(bd(1.0), bd(1.0));
            assertEquals(bd(0.0000), result);
        }

        @Test
        @DisplayName("精确计算: 1.1234 → 1.3456，收益率 19.7791%")
        void precision_case() {
            BigDecimal result = FundAnalyticsEngine.cumulativeReturn(bd(1.1234), bd(1.3456));
            // (1.3456 - 1.1234) / 1.1234 = 0.2222 / 1.1234 = 0.197791...
            assertEquals(new BigDecimal("19.7792"), result);
        }

        @Test
        @DisplayName("期初净值为null抛异常")
        void null_start_nav() {
            assertThrows(IllegalArgumentException.class,
                    () -> FundAnalyticsEngine.cumulativeReturn(null, bd(1.0)));
        }

        @Test
        @DisplayName("期初净值为0抛异常")
        void zero_start_nav() {
            assertThrows(IllegalArgumentException.class,
                    () -> FundAnalyticsEngine.cumulativeReturn(bd(0), bd(1.0)));
        }

        @Test
        @DisplayName("期初净值为负抛异常")
        void negative_start_nav() {
            assertThrows(IllegalArgumentException.class,
                    () -> FundAnalyticsEngine.cumulativeReturn(bd(-1.0), bd(1.0)));
        }
    }

    // ========================================================================
    // 2. 年化收益率测试
    // ========================================================================
    @Nested
    @DisplayName("年化收益率 (Annualized Return)")
    class AnnualizedReturnTest {

        @Test
        @DisplayName("1年期: 累计20% → 年化20%")
        void one_year() {
            BigDecimal result = FundAnalyticsEngine.annualizedReturn(bd(1.0), bd(1.2), 365);
            // (1.2/1.0)^(365/365) - 1 = 0.2 = 20%
            assertEquals(new BigDecimal("20.0000"), result);
        }

        @Test
        @DisplayName("半年期: 累计10% → 年化21%（复利效应）")
        void half_year() {
            BigDecimal result = FundAnalyticsEngine.annualizedReturn(bd(1.0), bd(1.1), 182);
            // (1.1)^(365/182) - 1 = (1.1)^2.0055 - 1 ≈ 0.2109 = 21.09%
            assertTrue(result.compareTo(bd(21.0)) > 0);
            assertTrue(result.compareTo(bd(21.2)) < 0);
        }

        @Test
        @DisplayName("2年期: 累计44% → 年化20%")
        void two_years() {
            // 1.2^2 = 1.44，所以2年累计44%对应年化20%
            BigDecimal result = FundAnalyticsEngine.annualizedReturn(bd(1.0), bd(1.44), 730);
            assertTrue(result.compareTo(bd(19.9)) > 0);
            assertTrue(result.compareTo(bd(20.1)) < 0);
        }

        @Test
        @DisplayName("持有0天返回0")
        void zero_days() {
            BigDecimal result = FundAnalyticsEngine.annualizedReturn(bd(1.0), bd(1.0), 0);
            assertEquals(bd(0.0000), result);
        }

        @Test
        @DisplayName("持有负数天返回0")
        void negative_days() {
            BigDecimal result = FundAnalyticsEngine.annualizedReturn(bd(1.0), bd(1.0), -10);
            assertEquals(bd(0.0000), result);
        }
    }

    // ========================================================================
    // 3. 最大回撤测试
    // ========================================================================
    @Nested
    @DisplayName("最大回撤 (Maximum Drawdown)")
    class MaxDrawdownTest {

        @Test
        @DisplayName("经典回撤: 1.0→1.5→1.2→1.8→1.3，最大回撤从1.5到1.2 = 20%")
        void classic_drawdown() {
            List<BigDecimal> navs = bdList(1.0, 1.5, 1.2, 1.8, 1.3);
            FundAnalyticsEngine.MaxDrawdownResult result = FundAnalyticsEngine.maxDrawdown(navs);

            // 从1.5跌到1.2: (1.5-1.2)/1.5 = 0.2 = 20%
            assertEquals(new BigDecimal("20.0000"), result.drawdown());
            assertEquals(1, result.startIndex()); // peak at index 1 (1.5)
            assertEquals(2, result.endIndex());   // trough at index 2 (1.2)
        }

        @Test
        @DisplayName("持续上涨: 无回撤")
        void no_drawdown() {
            List<BigDecimal> navs = bdList(1.0, 1.1, 1.2, 1.3, 1.4);
            FundAnalyticsEngine.MaxDrawdownResult result = FundAnalyticsEngine.maxDrawdown(navs);

            assertEquals(new BigDecimal("0.0000"), result.drawdown());
        }

        @Test
        @DisplayName("持续下跌: 最大回撤从第一个到最后一个")
        void continuous_decline() {
            List<BigDecimal> navs = bdList(1.0, 0.9, 0.8, 0.7, 0.6);
            FundAnalyticsEngine.MaxDrawdownResult result = FundAnalyticsEngine.maxDrawdown(navs);

            // (1.0 - 0.6) / 1.0 = 0.4 = 40%
            assertEquals(new BigDecimal("40.0000"), result.drawdown());
        }

        @Test
        @DisplayName("V型走势: 1.0→0.5→1.0，回撤50%")
        void v_shape() {
            List<BigDecimal> navs = bdList(1.0, 0.5, 1.0);
            FundAnalyticsEngine.MaxDrawdownResult result = FundAnalyticsEngine.maxDrawdown(navs);

            assertEquals(new BigDecimal("50.0000"), result.drawdown());
        }

        @Test
        @DisplayName("单个数据点返回0")
        void single_point() {
            List<BigDecimal> navs = bdList(1.0);
            FundAnalyticsEngine.MaxDrawdownResult result = FundAnalyticsEngine.maxDrawdown(navs);

            assertEquals(new BigDecimal("0.0000"), result.drawdown());
        }

        @Test
        @DisplayName("null输入返回0")
        void null_input() {
            FundAnalyticsEngine.MaxDrawdownResult result = FundAnalyticsEngine.maxDrawdown(null);
            assertEquals(new BigDecimal("0.0000"), result.drawdown());
        }
    }

    // ========================================================================
    // 4. 波动率测试
    // ========================================================================
    @Nested
    @DisplayName("波动率 (Volatility)")
    class VolatilityTest {

        @Test
        @DisplayName("恒定净值: 波动率为0")
        void constant_nav() {
            List<BigDecimal> navs = bdList(1.0, 1.0, 1.0, 1.0, 1.0);
            BigDecimal result = FundAnalyticsEngine.volatility(navs);
            assertEquals(new BigDecimal("0.0000"), result);
        }

        @Test
        @DisplayName("稳定上涨: 低波动率")
        void steady_growth() {
            List<BigDecimal> navs = bdList(1.0, 1.01, 1.02, 1.03, 1.04, 1.05);
            BigDecimal result = FundAnalyticsEngine.volatility(navs);
            // 日收益率稳定在~1%，年化波动率应很低
            assertTrue(result.compareTo(bd(5.0)) > 0);
            assertTrue(result.compareTo(bd(10.0)) < 0);
        }

        @Test
        @DisplayName("剧烈波动: 高波动率")
        void high_volatility() {
            List<BigDecimal> navs = bdList(1.0, 1.1, 0.9, 1.1, 0.9, 1.1, 0.9);
            BigDecimal result = FundAnalyticsEngine.volatility(navs);
            // 日收益率约±10%，年化波动率应很高
            assertTrue(result.compareTo(bd(100.0)) > 0);
        }

        @Test
        @DisplayName("数据不足返回0")
        void insufficient_data() {
            List<BigDecimal> navs = bdList(1.0);
            BigDecimal result = FundAnalyticsEngine.volatility(navs);
            assertEquals(new BigDecimal("0.0000"), result);
        }
    }

    // ========================================================================
    // 5. Sharpe Ratio 测试
    // ========================================================================
    @Nested
    @DisplayName("夏普比率 (Sharpe Ratio)")
    class SharpeRatioTest {

        @Test
        @DisplayName("收益20% 波动率15% 无风险2%: Sharpe = (20-2)/15 = 1.2")
        void normal_case() {
            BigDecimal result = FundAnalyticsEngine.sharpeRatio(bd(20), bd(15), bd(2));
            assertEquals(new BigDecimal("1.2000"), result);
        }

        @Test
        @DisplayName("收益等于无风险: Sharpe = 0")
        void zero_excess() {
            BigDecimal result = FundAnalyticsEngine.sharpeRatio(bd(2), bd(15), bd(2));
            assertEquals(new BigDecimal("0.0000"), result);
        }

        @Test
        @DisplayName("收益低于无风险: Sharpe 为负")
        void negative_sharpe() {
            BigDecimal result = FundAnalyticsEngine.sharpeRatio(bd(1), bd(15), bd(2));
            assertTrue(result.compareTo(BigDecimal.ZERO) < 0);
        }

        @Test
        @DisplayName("波动率为0: 返回0")
        void zero_volatility() {
            BigDecimal result = FundAnalyticsEngine.sharpeRatio(bd(20), bd(0), bd(2));
            assertEquals(new BigDecimal("0.0000"), result);
        }
    }

    // ========================================================================
    // 6. Sortino Ratio 测试
    // ========================================================================
    @Nested
    @DisplayName("索提诺比率 (Sortino Ratio)")
    class SortinoRatioTest {

        @Test
        @DisplayName("全部正收益: 无下行偏差，返回0")
        void all_positive_returns() {
            List<BigDecimal> navs = bdList(1.0, 1.05, 1.10, 1.15, 1.20);
            BigDecimal result = FundAnalyticsEngine.sortinoRatio(navs, bd(20), bd(2));
            // 所有日收益率都为正，且大于无风险日利率，无下行偏差
            assertEquals(new BigDecimal("0.0000"), result);
        }

        @Test
        @DisplayName("有下行风险: Sortino > 0")
        void with_downside() {
            // 先跌后涨，有下行波动
            List<BigDecimal> navs = bdList(1.0, 0.9, 1.0, 0.95, 1.1, 1.2);
            BigDecimal result = FundAnalyticsEngine.sortinoRatio(navs, bd(20), bd(2));
            assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
        }
    }

    // ========================================================================
    // 7. Beta 测试
    // ========================================================================
    @Nested
    @DisplayName("Beta 系数")
    class BetaTest {

        @Test
        @DisplayName("完全同步: Beta = 1")
        void perfect_sync() {
            List<BigDecimal> fund = bdList(1.0, 1.1, 1.2, 1.3, 1.4);
            List<BigDecimal> market = bdList(1.0, 1.1, 1.2, 1.3, 1.4);
            BigDecimal result = FundAnalyticsEngine.beta(fund, market);
            // 完全正相关，Beta ≈ 1
            assertTrue(result.subtract(bd(1.0)).abs().compareTo(bd(0.01)) < 0);
        }

        @Test
        @DisplayName("基金波动是市场的2倍: Beta ≈ 2")
        void double_volatility() {
            List<BigDecimal> fund = bdList(1.0, 1.2, 1.0, 1.2, 1.0);
            List<BigDecimal> market = bdList(1.0, 1.1, 1.0, 1.1, 1.0);
            BigDecimal result = FundAnalyticsEngine.beta(fund, market);
            assertTrue(result.subtract(bd(2.0)).abs().compareTo(bd(0.01)) < 0);
        }

        @Test
        @DisplayName("反向运动: Beta < 0")
        void inverse_movement() {
            List<BigDecimal> fund = bdList(1.0, 0.9, 1.0, 0.9, 1.0);
            List<BigDecimal> market = bdList(1.0, 1.1, 1.0, 1.1, 1.0);
            BigDecimal result = FundAnalyticsEngine.beta(fund, market);
            assertTrue(result.compareTo(BigDecimal.ZERO) < 0);
        }

        @Test
        @DisplayName("数据不足返回1")
        void insufficient_data() {
            List<BigDecimal> fund = bdList(1.0);
            List<BigDecimal> market = bdList(1.0);
            BigDecimal result = FundAnalyticsEngine.beta(fund, market);
            assertEquals(bd(1.0).setScale(SCALE, ROUND), result);
        }
    }

    // ========================================================================
    // 8. Alpha 测试
    // ========================================================================
    @Nested
    @DisplayName("Alpha (Jensen's Alpha)")
    class AlphaTest {

        @Test
        @DisplayName("基金跑赢基准: Alpha > 0")
        void positive_alpha() {
            // 基金年化25%，基准年化15%，无风险2%，Beta=1
            // α = 25 - [2 + 1×(15-2)] = 25 - 15 = 10
            BigDecimal result = FundAnalyticsEngine.alpha(bd(25), bd(15), bd(2), bd(1));
            assertEquals(new BigDecimal("10.0000"), result);
        }

        @Test
        @DisplayName("基金跑输基准: Alpha < 0")
        void negative_alpha() {
            // 基金年化10%，基准年化15%，无风险2%，Beta=1
            // α = 10 - [2 + 1×(15-2)] = 10 - 15 = -5
            BigDecimal result = FundAnalyticsEngine.alpha(bd(10), bd(15), bd(2), bd(1));
            assertEquals(new BigDecimal("-5.0000"), result);
        }

        @Test
        @DisplayName("基金与基准一致: Alpha ≈ 0")
        void zero_alpha() {
            // 基金年化15%，基准年化15%，无风险2%，Beta=1
            // α = 15 - [2 + 1×(15-2)] = 15 - 15 = 0
            BigDecimal result = FundAnalyticsEngine.alpha(bd(15), bd(15), bd(2), bd(1));
            assertEquals(new BigDecimal("0.0000"), result);
        }

        @Test
        @DisplayName("高Beta基金: Alpha计算正确")
        void high_beta_alpha() {
            // 基金年化30%，基准年化10%，无风险2%，Beta=1.5
            // α = 30 - [2 + 1.5×(10-2)] = 30 - 14 = 16
            BigDecimal result = FundAnalyticsEngine.alpha(bd(30), bd(10), bd(2), bd(1.5));
            assertEquals(new BigDecimal("16.0000"), result);
        }
    }

    // ========================================================================
    // 辅助方法测试
    // ========================================================================
    @Nested
    @DisplayName("辅助方法")
    class UtilityTest {

        @Test
        @DisplayName("日收益率计算")
        void daily_returns() {
            List<BigDecimal> navs = bdList(1.0, 1.1, 1.21);
            List<BigDecimal> returns = FundAnalyticsEngine.dailyReturns(navs);

            assertEquals(2, returns.size());
            // (1.1-1.0)/1.0 = 0.1
            assertTrue(returns.get(0).subtract(bd(0.1)).abs().compareTo(bd(0.0001)) < 0);
            // (1.21-1.1)/1.1 = 0.1
            assertTrue(returns.get(1).subtract(bd(0.1)).abs().compareTo(bd(0.0001)) < 0);
        }

        @Test
        @DisplayName("均值计算")
        void mean() {
            List<BigDecimal> values = bdList(1.0, 2.0, 3.0, 4.0, 5.0);
            BigDecimal result = FundAnalyticsEngine.mean(values);
            assertEquals(bd(3.0), result);
        }

        @Test
        @DisplayName("方差计算")
        void variance() {
            List<BigDecimal> values = bdList(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
            BigDecimal mean = FundAnalyticsEngine.mean(values);
            BigDecimal result = FundAnalyticsEngine.variance(values, mean);
            // 样本方差 = 4.0
            assertTrue(result.subtract(bd(4.0)).abs().compareTo(bd(0.01)) < 0);
        }

        @Test
        @DisplayName("平方根计算")
        void sqrt() {
            BigDecimal result = FundAnalyticsEngine.bigSqrt(bd(2.0), java.math.MathContext.DECIMAL128);
            assertTrue(result.subtract(bd(1.41421356)).abs().compareTo(bd(0.0001)) < 0);
        }

        @Test
        @DisplayName("平方根 of 0 = 0")
        void sqrt_zero() {
            BigDecimal result = FundAnalyticsEngine.bigSqrt(bd(0), java.math.MathContext.DECIMAL128);
            assertEquals(bd(0), result);
        }

        @Test
        @DisplayName("负数平方根抛异常")
        void sqrt_negative() {
            assertThrows(ArithmeticException.class,
                    () -> FundAnalyticsEngine.bigSqrt(bd(-1), java.math.MathContext.DECIMAL128));
        }
    }
}
