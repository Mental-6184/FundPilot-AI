package com.fundpilot.agent.report;

/**
 * 分析报告 Markdown 模板
 * 四种报告类型：基金分析、基金对比、组合诊断、基金推荐
 *
 * <p>模板使用 ${key} 占位符，由 ReportGenerator 渲染</p>
 */
public final class ReportTemplates {

    private ReportTemplates() {}

    // ========================================================================
    // 通用片段
    // ========================================================================

    /** 报告头部 */
    public static final String HEADER = """
            # ${reportTitle}

            > 报告类型：${reportType} | 生成时间：${generateTime} | 仅供参考，不构成投资建议

            ---

            """;

    /** 风险提示（所有报告通用） */
    public static final String RISK_DISCLAIMER = """

            ---

            ### ⚠️ 风险提示

            1. 本报告由 AI 系统自动生成，基于历史数据分析，不构成任何投资建议
            2. 基金过往业绩不代表未来表现，投资有风险，入市需谨慎
            3. 投资者应根据自身风险承受能力，独立做出投资决策
            4. 报告中的数据来源于公开市场信息，可能存在延迟或误差
            5. 建议投资者在做出投资决策前，咨询专业理财顾问

            ---
            *FundPilot AI 智能分析系统 | ${generateTime}*
            """;

    // ========================================================================
    // 1. 基金分析报告
    // ========================================================================

    public static final String FUND_ANALYSIS = """
            ${header}

            ### 📋 摘要

            ${fundName}（${fundCode}）是一只${fundType}基金，风险等级为${riskLevel}。
            当前最新净值为 ${latestNav}，基金规模 ${fundSize} 亿元。
            ${summaryConclusion}

            ---

            ### 📈 收益分析

            | 周期 | 收益率 | 评价 |
            |------|--------|------|
            | 近1周 | ${return1w} | ${return1wComment} |
            | 近1月 | ${return1m} | ${return1mComment} |
            | 近3月 | ${return3m} | ${return3mComment} |
            | 近6月 | ${return6m} | ${return6mComment} |
            | 近1年 | ${return1y} | ${return1yComment} |
            | 近3年 | ${return3y} | ${return3yComment} |
            | 成立以来 | ${returnSince} | 年化 ${annualizedReturn} |

            **收益分析要点：**
            ${returnAnalysis}

            ---

            ### ⚠️ 风险分析

            | 指标 | 数值 | 解读 |
            |------|------|------|
            | 年化波动率 | ${volatility} | ${volatilityComment} |
            | 最大回撤 | ${maxDrawdown} | ${maxDrawdownComment} |
            | 夏普比率 | ${sharpeRatio} | ${sharpeComment} |
            | 索提诺比率 | ${sortinoRatio} | ${sortinoComment} |
            | 卡尔马比率 | ${calmarRatio} | ${calmarComment} |
            | Alpha | ${alpha} | ${alphaComment} |
            | Beta | ${beta} | ${betaComment} |

            **风险分析要点：**
            ${riskAnalysis}

            ---

            ### 🏦 持仓分析

            **前十大重仓股：**

            | 排名 | 股票代码 | 股票名称 | 持仓占比 |
            |------|----------|----------|----------|
            ${topHoldings}

            **持仓分析要点：**
            ${holdingAnalysis}

            ---

            ### 👤 基金经理

            - **姓名：** ${managerName}
            - **从业年限：** ${managerYears} 年
            - **投资风格：** ${managerStyle}
            - **管理规模：** ${managerSize} 亿元

            **经理评价：**
            ${managerComment}

            ---

            ### ✅ 优点

            ${pros}

            ### ❌ 缺点

            ${cons}

            ---

            ### 💡 投资建议

            ${investmentAdvice}

            ${riskDisclaimer}
            """;

    // ========================================================================
    // 2. 基金对比报告
    // ========================================================================

    public static final String FUND_COMPARISON = """
            ${header}

            ### 📋 摘要

            本次对比共 ${fundCount} 只基金：${fundNames}。
            ${summaryConclusion}

            ---

            ### 📊 基本信息对比

            | 基金代码 | 基金名称 | 类型 | 风险等级 | 规模(亿) | 最新净值 |
            |----------|----------|------|----------|----------|----------|
            ${basicInfoTable}

            ---

            ### 📈 收益对比

            | 基金代码 | 近1周 | 近1月 | 近3月 | 近6月 | 近1年 | 成立以来 |
            |----------|-------|-------|-------|-------|-------|----------|
            ${returnTable}

            **收益对比分析：**
            ${returnComparison}

            ---

            ### ⚠️ 风险对比

            | 基金代码 | 波动率 | 最大回撤 | 夏普比率 | 索提诺比率 |
            |----------|--------|----------|----------|------------|
            ${riskTable}

            **风险对比分析：**
            ${riskComparison}

            ---

            ### 🏦 持仓对比

            **重仓股重合度：** ${overlapStocks} 只重仓股重合，重合度 ${overlapRatio}

            ${holdingComparison}

            ---

            ### 🏆 综合排名

            | 排名 | 基金代码 | 基金名称 | 综合评分 | 推荐理由 |
            |------|----------|----------|----------|----------|
            ${rankingTable}

            ---

            ### ✅ 各基金优点

            ${prosList}

            ### ❌ 各基金缺点

            ${consList}

            ---

            ### 💡 投资建议

            ${investmentAdvice}

            ${riskDisclaimer}
            """;

    // ========================================================================
    // 3. 组合诊断报告
    // ========================================================================

    public static final String PORTFOLIO_DIAGNOSIS = """
            ${header}

            ### 📋 摘要

            组合「${portfolioName}」包含 ${fundCount} 只基金，总投入 ${totalInvest} 元。
            当前总市值 ${totalValue} 元，总盈亏 ${totalProfit} 元。
            风险评分 ${riskScore}/100（${riskLevelName}）。
            ${summaryConclusion}

            ---

            ### 📈 收益分析

            | 指标 | 数值 |
            |------|------|
            | 累计收益率 | ${cumulativeReturn} |
            | 年化收益率 | ${annualizedReturn} |
            | 总盈亏 | ${totalProfit} |
            | 夏普比率 | ${sharpeRatio} |

            **收益分析要点：**
            ${returnAnalysis}

            ---

            ### ⚠️ 风险分析

            | 指标 | 数值 | 评价 |
            |------|------|------|
            | 组合波动率 | ${volatility} | ${volatilityComment} |
            | 最大回撤 | ${maxDrawdown} | ${maxDrawdownComment} |
            | 夏普比率 | ${sharpeRatio} | ${sharpeComment} |

            **风险分析要点：**
            ${riskAnalysis}

            ---

            ### 🎯 风险评分详情

            | 维度 | 得分 | 权重 | 加权得分 | 评价 |
            |------|------|------|----------|------|
            | 波动率风险 | ${volScore} | ${volWeight} | ${volWeighted} | ${volComment} |
            | 回撤风险 | ${mddScore} | ${mddWeight} | ${mddWeighted} | ${mddComment} |
            | 集中度风险 | ${conScore} | ${conWeight} | ${conWeighted} | ${conComment} |
            | 基金风险等级 | ${fundScore} | ${fundWeight} | ${fundWeighted} | ${fundComment} |

            **综合评分：${riskScore}/100 → ${riskLevelName}**

            ---

            ### 🏭 行业分布

            | 类型 | 占比 | 基金数量 | 评价 |
            |------|------|----------|------|
            ${industryTable}

            **分布评价：**
            ${industryComment}

            ---

            ### 🔗 重仓股重合度

            - 重合度评分：${overlapScore}%
            - 重合股票数：${overlapCount} 只

            **重合详情：**
            ${overlapDetail}

            **重合度评价：**
            ${overlapComment}

            ---

            ### 🔍 持仓详情

            | 基金代码 | 基金名称 | 投入金额 | 占比 | 当前收益 |
            |----------|----------|----------|------|----------|
            ${fundListTable}

            ---

            ### ✅ 组合优点

            ${pros}

            ### ❌ 组合缺点

            ${cons}

            ---

            ### 💡 优化建议

            ${optimizationAdvice}

            ${riskDisclaimer}
            """;

    // ========================================================================
    // 4. 基金推荐报告
    // ========================================================================

    public static final String FUND_RECOMMENDATION = """
            ${header}

            ### 📋 摘要

            **用户需求：** ${userDemand}
            **筛选条件：** ${filterCondition}
            **推荐策略：** ${strategy}

            共筛选出 ${fundCount} 只符合条件的基金。
            ${summaryConclusion}

            ---

            ### 🎯 推荐基金列表

            | 排名 | 基金代码 | 基金名称 | 类型 | 风险等级 | 近1年收益 | 夏普比率 |
            |------|----------|----------|------|----------|-----------|----------|
            ${recommendTable}

            ---

            ### 📊 推荐理由

            ${recommendReasons}

            ---

            ### 📈 收益分析

            | 基金代码 | 近1周 | 近1月 | 近3月 | 近6月 | 近1年 |
            |----------|-------|-------|-------|-------|-------|
            ${returnTable}

            ---

            ### ⚠️ 风险分析

            | 基金代码 | 波动率 | 最大回撤 | 夏普比率 |
            |----------|--------|----------|----------|
            ${riskTable}

            ---

            ### ✅ 推荐优点

            ${pros}

            ### ❌ 需要注意

            ${cons}

            ---

            ### 💡 投资建议

            **配置建议：**
            ${allocationAdvice}

            **操作建议：**
            ${operationAdvice}

            ${riskDisclaimer}
            """;
}
