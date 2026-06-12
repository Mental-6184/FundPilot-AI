<template>
  <div class="portfolio-analysis" v-loading="portfolioStore.loading">
    <!-- Back Button -->
    <div class="back-btn" @click="router.back()">
      <el-icon><ArrowLeft /></el-icon>
      <span>返回</span>
    </div>

    <template v-if="analysis">
      <!-- Portfolio Header -->
      <div class="analysis-header glass-card animate-fade-in-up">
        <h1 class="analysis-title">{{ analysis.portfolioName }}</h1>
        <p class="analysis-desc">投资组合分析报告</p>
      </div>

      <!-- Metric Cards -->
      <div class="metrics-grid stagger-children">
        <div class="metric-card">
          <div class="metric-icon" style="background: rgba(59, 130, 246, 0.1)">
            <el-icon :size="20" style="color: #3B82F6"><TrendCharts /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">累计收益率</div>
            <div class="metric-value" :class="getReturnClass(analysis.cumulativeReturn)">
              {{ formatPct(analysis.cumulativeReturn) }}
            </div>
          </div>
        </div>
        <div class="metric-card">
          <div class="metric-icon" style="background: rgba(34, 197, 94, 0.1)">
            <el-icon :size="20" style="color: #22C55E"><Wallet /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">总盈亏</div>
            <div class="metric-value" :class="getReturnClass(analysis.totalProfit)">
              {{ analysis.totalProfit?.toFixed(2) || '--' }} 元
            </div>
          </div>
        </div>
        <div class="metric-card">
          <div class="metric-icon" style="background: rgba(239, 68, 68, 0.1)">
            <el-icon :size="20" style="color: #EF4444"><Bottom /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">最大回撤</div>
            <div class="metric-value down">{{ formatPct(analysis.maxDrawdown) }}</div>
          </div>
        </div>
        <div class="metric-card">
          <div class="metric-icon" style="background: rgba(139, 92, 246, 0.1)">
            <el-icon :size="20" style="color: #8B5CF6"><DataAnalysis /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">风险评分</div>
            <div class="metric-value" :class="getRiskScoreClass(analysis.riskScore)">
              {{ analysis.riskScore }}/100
            </div>
            <div class="metric-sub">{{ analysis.riskLevelName }}</div>
          </div>
        </div>
      </div>

      <!-- Risk Metrics -->
      <div class="risk-section glass-card">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Warning /></el-icon>
            <span>风险指标</span>
          </h3>
        </div>
        <div class="risk-metrics">
          <div class="risk-metric">
            <span class="risk-label">年化波动率</span>
            <span class="risk-value" :class="getReturnClass(analysis.volatility)">{{ formatPct(analysis.volatility) }}</span>
          </div>
          <div class="risk-metric">
            <span class="risk-label">最大回撤</span>
            <span class="risk-value down">{{ formatPct(analysis.maxDrawdown) }}</span>
          </div>
          <div class="risk-metric">
            <span class="risk-label">夏普比率</span>
            <span class="risk-value">{{ analysis.sharpeRatio?.toFixed(4) || '--' }}</span>
          </div>
        </div>
      </div>

      <!-- Risk Radar -->
      <div class="radar-section glass-card" v-if="analysis.riskScoreDetail">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><DataAnalysis /></el-icon>
            <span>风险评分详情</span>
          </h3>
        </div>
        <div class="radar-content">
          <div ref="riskRadarRef" class="radar-chart"></div>
          <div class="risk-table">
            <div class="risk-row header">
              <div class="risk-cell">维度</div>
              <div class="risk-cell">得分</div>
              <div class="risk-cell">权重</div>
              <div class="risk-cell">评价</div>
            </div>
            <div class="risk-row" v-for="item in riskScoreTable" :key="item.dimension">
              <div class="risk-cell">{{ item.dimension }}</div>
              <div class="risk-cell">
                <span class="score-badge" :class="item.score > 60 ? 'high' : 'low'">{{ item.score }}</span>
              </div>
              <div class="risk-cell">{{ item.weight }}</div>
              <div class="risk-cell">
                <span class="comment-text">{{ item.comment }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Industry Distribution -->
      <div class="industry-section glass-card" v-if="analysis.industryDistribution?.length">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><PieChart /></el-icon>
            <span>行业分布</span>
          </h3>
        </div>
        <div class="industry-content">
          <div ref="industryPieRef" class="pie-chart"></div>
          <div class="industry-table">
            <div class="industry-row header">
              <div class="industry-cell">类型</div>
              <div class="industry-cell">占比</div>
              <div class="industry-cell">基金数</div>
            </div>
            <div class="industry-row" v-for="item in analysis.industryDistribution" :key="item.industry">
              <div class="industry-cell">{{ item.industry }}</div>
              <div class="industry-cell">
                <span class="mono">{{ item.ratio?.toFixed(2) }}%</span>
              </div>
              <div class="industry-cell">{{ item.fundCount }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Holdings Overlap -->
      <div class="overlap-section glass-card" v-if="analysis.holdingOverlaps?.length">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Connection /></el-icon>
            <span>重仓股重合度（{{ analysis.overlapScore }}%）</span>
          </h3>
        </div>
        <div class="overlap-grid">
          <div class="overlap-row header">
            <div class="overlap-cell">股票代码</div>
            <div class="overlap-cell">股票名称</div>
            <div class="overlap-cell">出现次数</div>
            <div class="overlap-cell">涉及基金</div>
          </div>
          <div class="overlap-row" v-for="item in analysis.holdingOverlaps" :key="item.stockCode">
            <div class="overlap-cell">
              <span class="stock-code">{{ item.stockCode }}</span>
            </div>
            <div class="overlap-cell">{{ item.stockName }}</div>
            <div class="overlap-cell">
              <span class="count-badge">{{ item.appearCount }}</span>
            </div>
            <div class="overlap-cell">
              <span class="fund-tag" v-for="code in item.fundCodes" :key="code">{{ code }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Fund Items -->
      <div class="items-section glass-card" v-if="analysis.fundItems?.length">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><List /></el-icon>
            <span>持仓详情</span>
          </h3>
        </div>
        <div class="items-grid">
          <div class="items-row header">
            <div class="items-cell code">基金代码</div>
            <div class="items-cell name">基金名称</div>
            <div class="items-cell amount">投入金额</div>
            <div class="items-cell ratio">占比</div>
            <div class="items-cell profit">盈亏</div>
            <div class="items-cell action">操作</div>
          </div>
          <div class="items-row" v-for="item in analysis.fundItems" :key="item.fundCode">
            <div class="items-cell code">
              <span class="fund-code">{{ item.fundCode }}</span>
            </div>
            <div class="items-cell name">{{ item.fundName }}</div>
            <div class="items-cell amount">
              <span class="mono">{{ item.investAmount?.toFixed(2) }}</span>
            </div>
            <div class="items-cell ratio">
              <span class="mono">{{ item.actualRatio?.toFixed(2) }}%</span>
            </div>
            <div class="items-cell profit">
              <span :class="getReturnClass(item.profit)" class="mono">{{ item.profit?.toFixed(2) }}</span>
            </div>
            <div class="items-cell action">
              <button class="detail-btn" @click="router.push(`/fund/${item.fundCode}`)">详情</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Generate Report -->
      <div class="report-section glass-card">
        <button class="report-btn" :loading="reportLoading" @click="generateReport">
          <el-icon><Document /></el-icon>
          <span>生成诊断报告</span>
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePortfolioStore } from '@/stores/portfolio'
import { getPortfolioReport } from '@/api/analytics'
import { formatPercent, getReturnClass } from '@/utils/format'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()
const portfolioStore = usePortfolioStore()

const riskRadarRef = ref(null)
const industryPieRef = ref(null)
const reportLoading = ref(false)

const analysis = computed(() => portfolioStore.analysisData)
const portfolioId = computed(() => route.params.portfolioId)

const riskScoreTable = computed(() => {
  if (!analysis.value?.riskScoreDetail) return []
  const d = analysis.value.riskScoreDetail
  return [
    { dimension: '波动率风险', score: d.volatilityScore, weight: d.volatilityWeight, comment: d.volatilityScore > 60 ? '波动较大' : '波动可控' },
    { dimension: '回撤风险', score: d.drawdownScore, weight: d.drawdownWeight, comment: d.drawdownScore > 60 ? '回撤较大' : '回撤可控' },
    { dimension: '集中度风险', score: d.concentrationScore, weight: d.concentrationWeight, comment: d.concentrationScore > 60 ? '集中度较高' : '分散良好' },
    { dimension: '基金风险等级', score: d.fundRiskScore, weight: d.fundRiskWeight, comment: d.fundRiskScore > 60 ? '风险偏高' : '风险适中' },
  ]
})

onMounted(async () => {
  await portfolioStore.fetchAnalysis(portfolioId.value)
  nextTick(() => {
    renderRiskRadar()
    renderIndustryPie()
  })
})

function renderRiskRadar() {
  if (!riskRadarRef.value || !analysis.value?.riskScoreDetail) return
  const d = analysis.value.riskScoreDetail
  const chart = echarts.init(riskRadarRef.value)
  chart.setOption({
    radar: {
      indicator: [
        { name: '波动率', max: 100 },
        { name: '回撤', max: 100 },
        { name: '集中度', max: 100 },
        { name: '基金风险', max: 100 },
      ],
      axisName: { color: '#94A3B8', fontSize: 12 },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
      splitArea: { areaStyle: { color: ['rgba(59, 130, 246, 0.02)', 'rgba(59, 130, 246, 0.04)'] } },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: [d.volatilityScore, d.drawdownScore, d.concentrationScore, d.fundRiskScore],
        name: '风险评分',
        areaStyle: { color: 'rgba(59, 130, 246, 0.2)' },
        lineStyle: { color: '#3B82F6', width: 2 },
        itemStyle: { color: '#3B82F6' },
      }],
    }],
  })
  window.addEventListener('resize', () => chart.resize())
}

function renderIndustryPie() {
  if (!industryPieRef.value || !analysis.value?.industryDistribution?.length) return
  const chart = echarts.init(industryPieRef.value)
  const colors = ['#3B82F6', '#8B5CF6', '#22C55E', '#F59E0B', '#EF4444', '#06B6D4', '#EC4899']
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {d}%',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: analysis.value.industryDistribution.map((item, i) => ({
        name: item.industry,
        value: item.ratio,
        itemStyle: { color: colors[i % colors.length] },
      })),
      label: { color: '#94A3B8', fontSize: 12 },
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.3)' },
      },
    }],
  })
  window.addEventListener('resize', () => chart.resize())
}

async function generateReport() {
  reportLoading.value = true
  try {
    await getPortfolioReport(portfolioId.value)
    router.push(`/report/portfolio/${portfolioId.value}`)
  } finally {
    reportLoading.value = false
  }
}

function formatPct(val) {
  return formatPercent(val)
}

function getRiskScoreClass(score) {
  if (score <= 30) return 'up'
  if (score <= 60) return 'warning'
  return 'down'
}
</script>

<style scoped>
.portfolio-analysis {
  max-width: 1200px;
  margin: 0 auto;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  margin-bottom: 16px;
  border-radius: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: var(--color-surface);
  color: var(--color-text);
}

/* Analysis Header */
.analysis-header {
  padding: 28px;
  margin-bottom: 20px;
}

.analysis-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 4px;
}

.analysis-desc {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

/* Metrics Grid */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.metric-card {
  background: var(--color-card);
  border: 1px solid var(--color-glass);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  border-color: var(--color-glass-hover);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metric-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-bottom: 4px;
}

.metric-value {
  font-size: 22px;
  font-weight: 700;
  font-family: var(--font-mono);
  color: var(--color-text);
}

.metric-value.up { color: #22C55E; }
.metric-value.down { color: #EF4444; }
.metric-value.warning { color: #F59E0B; }

.metric-sub {
  font-size: 11px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

/* Sections */
.glass-card {
  margin-bottom: 16px;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

/* Risk Metrics */
.risk-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.risk-metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 10px;
}

.risk-label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.risk-value {
  font-size: 15px;
  font-weight: 600;
  font-family: var(--font-mono);
  color: var(--color-text);
}

.risk-value.up { color: #22C55E; }
.risk-value.down { color: #EF4444; }

/* Radar Section */
.radar-content {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

.radar-chart {
  height: 300px;
}

.risk-table,
.industry-table,
.overlap-grid,
.items-grid {
  border: 1px solid var(--color-glass);
  border-radius: 10px;
  overflow: hidden;
}

.risk-row,
.industry-row,
.overlap-row,
.items-row {
  display: grid;
  border-bottom: 1px solid var(--color-glass);
}

.risk-row { grid-template-columns: 1fr 80px 80px 1fr; }
.industry-row { grid-template-columns: 1fr 100px 80px; }
.overlap-row { grid-template-columns: 120px 1fr 80px 1fr; }
.items-row { grid-template-columns: 100px 1fr 100px 80px 100px 80px; }

.risk-row:last-child,
.industry-row:last-child,
.overlap-row:last-child,
.items-row:last-child {
  border-bottom: none;
}

.risk-row.header,
.industry-row.header,
.overlap-row.header,
.items-row.header {
  background: var(--color-surface);
}

.risk-cell,
.industry-cell,
.overlap-cell,
.items-cell {
  padding: 12px 14px;
  font-size: 13px;
  color: var(--color-text);
}

.risk-cell:first-child,
.industry-cell:first-child,
.overlap-cell:first-child,
.items-cell:first-child {
  font-weight: 500;
  color: var(--color-text-secondary);
}

.score-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  font-family: var(--font-mono);
}

.score-badge.high {
  background: rgba(239, 68, 68, 0.1);
  color: #F87171;
}

.score-badge.low {
  background: rgba(34, 197, 94, 0.1);
  color: #4ADE80;
}

.count-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.fund-tag {
  display: inline-block;
  padding: 2px 8px;
  margin-right: 4px;
  background: var(--color-surface);
  border-radius: 4px;
  font-size: 11px;
  font-family: var(--font-mono);
  color: var(--color-text-secondary);
}

.fund-code {
  font-family: var(--font-mono);
  color: #3B82F6;
}

.mono {
  font-family: var(--font-mono);
}

.detail-btn {
  padding: 4px 10px;
  background: rgba(59, 130, 246, 0.1);
  border: none;
  border-radius: 6px;
  font-size: 12px;
  color: #3B82F6;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.detail-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

/* Industry Section */
.industry-content {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

.pie-chart {
  height: 300px;
}

/* Report Section */
.report-section {
  padding: 24px;
  display: flex;
  justify-content: center;
}

.report-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
}

.report-btn:hover {
  box-shadow: 0 6px 28px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}
</style>
