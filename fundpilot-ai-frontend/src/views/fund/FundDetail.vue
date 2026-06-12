<template>
  <div class="fund-detail" v-loading="fundStore.loading">
    <!-- Back Button -->
    <div class="back-btn" @click="router.back()">
      <el-icon><ArrowLeft /></el-icon>
      <span>返回</span>
    </div>

    <template v-if="fundStore.fundDetail">
      <!-- Fund Header -->
      <div class="fund-header glass-card animate-fade-in-up">
        <div class="fund-info">
          <div class="fund-tags">
            <span class="fund-type-tag">{{ fundStore.fundDetail.fundType }}</span>
            <span class="risk-tag" :class="riskClass">{{ fundStore.fundDetail.riskLevel }}</span>
          </div>
          <h1 class="fund-name">{{ fundStore.fundDetail.fundName }}</h1>
          <div class="fund-meta">
            <span class="fund-code">{{ fundStore.fundDetail.fundCode }}</span>
            <span class="meta-sep">·</span>
            <span class="fund-date">成立日期: {{ fundStore.fundDetail.establishDate }}</span>
          </div>
        </div>
        <div class="nav-display">
          <div class="nav-label">最新净值</div>
          <div class="nav-value">{{ fundStore.fundDetail.latestNav?.toFixed(4) }}</div>
          <div class="nav-date">累计净值: {{ fundStore.fundDetail.latestAccNav?.toFixed(4) || '--' }}</div>
        </div>
      </div>

      <!-- Performance Metrics -->
      <div class="metrics-grid stagger-children" v-if="fundStore.performance">
        <div class="metric-card" v-for="metric in performanceMetrics" :key="metric.label">
          <div class="metric-label">{{ metric.label }}</div>
          <div class="metric-value" :class="metric.class">{{ metric.value }}</div>
        </div>
      </div>

      <!-- Chart Section -->
      <div class="chart-section glass-card">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><DataLine /></el-icon>
            <span>净值走势</span>
          </h3>
        </div>
        <div ref="navChartRef" class="chart-container"></div>
      </div>

      <!-- Investment Strategy -->
      <div class="strategy-section glass-card" v-if="fundStore.fundDetail.investStrategy">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            <span>投资策略</span>
          </h3>
        </div>
        <p class="strategy-text">{{ fundStore.fundDetail.investStrategy }}</p>
      </div>

      <!-- Fund Manager -->
      <div class="manager-section glass-card" v-if="fundStore.fundDetail.manager">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><User /></el-icon>
            <span>基金经理</span>
          </h3>
        </div>
        <div class="manager-grid">
          <div class="manager-info">
            <div class="manager-avatar">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <div class="manager-details">
              <h4 class="manager-name">{{ fundStore.fundDetail.manager.name }}</h4>
              <p class="manager-company">{{ fundStore.fundDetail.manager.company }}</p>
            </div>
          </div>
          <div class="manager-stats">
            <div class="manager-stat">
              <span class="stat-label">任职年限</span>
              <span class="stat-value">{{ fundStore.fundDetail.manager.tenureYears }} 年</span>
            </div>
            <div class="manager-stat">
              <span class="stat-label">在管基金</span>
              <span class="stat-value">{{ fundStore.fundDetail.manager.manageCount }} 只</span>
            </div>
            <div class="manager-stat">
              <span class="stat-label">最佳回报</span>
              <span class="stat-value text-success">{{ formatPercent(fundStore.fundDetail.manager.bestReturn) }}</span>
            </div>
          </div>
        </div>
        <p v-if="fundStore.fundDetail.manager.biography" class="manager-bio">
          {{ fundStore.fundDetail.manager.biography }}
        </p>
      </div>

      <!-- Top Holdings -->
      <div class="holdings-section glass-card" v-if="fundStore.fundDetail.topHoldings?.length">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><List /></el-icon>
            <span>前十大持仓</span>
          </h3>
        </div>
        <div class="holdings-grid">
          <div class="holding-header">
            <div class="holding-cell code">股票代码</div>
            <div class="holding-cell name">股票名称</div>
            <div class="holding-cell ratio">持仓占比</div>
            <div class="holding-cell amount">持仓金额(万)</div>
          </div>
          <div
            v-for="holding in fundStore.fundDetail.topHoldings"
            :key="holding.stockCode"
            class="holding-row"
          >
            <div class="holding-cell code">
              <span class="stock-code">{{ holding.stockCode }}</span>
            </div>
            <div class="holding-cell name">{{ holding.stockName }}</div>
            <div class="holding-cell ratio">
              <div class="ratio-bar">
                <div class="ratio-fill" :style="{ width: holding.holdRatio + '%' }"></div>
              </div>
              <span class="ratio-value">{{ holding.holdRatio }}%</span>
            </div>
            <div class="holding-cell amount">{{ holding.holdAmount?.toLocaleString() }}</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFundStore } from '@/stores/fund'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()
const fundStore = useFundStore()
const navChartRef = ref(null)

const riskClass = computed(() => {
  const map = { LOW: 'low', MEDIUM_LOW: 'low', MEDIUM: 'medium', MEDIUM_HIGH: 'high', HIGH: 'high' }
  return map[fundStore.fundDetail?.riskLevel] || 'default'
})

const performanceMetrics = computed(() => {
  const p = fundStore.performance
  if (!p) return []
  return [
    { label: '近1周', value: formatPercent(p.return1w), class: getReturnClass(p.return1w) },
    { label: '近1月', value: formatPercent(p.return1m), class: getReturnClass(p.return1m) },
    { label: '近3月', value: formatPercent(p.return3m), class: getReturnClass(p.return3m) },
    { label: '近1年', value: formatPercent(p.return1y), class: getReturnClass(p.return1y) },
    { label: '年化收益', value: formatPercent(p.annualizedReturn), class: getReturnClass(p.annualizedReturn) },
    { label: '最大回撤', value: formatPercent(p.maxDrawdown), class: 'down' },
    { label: '夏普比率', value: p.sharpeRatio?.toFixed(4) || '--', class: '' },
    { label: '卡尔马比率', value: p.calmarRatio?.toFixed(4) || '--', class: '' },
  ]
})

onMounted(async () => {
  const fundCode = route.params.fundCode
  await fundStore.fetchFundDetail(fundCode)
  fundStore.fetchPerformance(fundCode)
  nextTick(() => renderNavChart())
})

function renderNavChart() {
  if (!navChartRef.value || !fundStore.fundDetail?.navTrend?.length) return

  const chart = echarts.init(navChartRef.value)
  const trend = fundStore.fundDetail.navTrend

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
    },
    xAxis: {
      type: 'category',
      data: trend.map((item) => item.date),
      axisLabel: { color: '#64748B', fontSize: 11 },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
    },
    yAxis: {
      type: 'value',
      scale: true,
      axisLabel: { color: '#64748B', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.04)' } },
    },
    series: [
      {
        name: '单位净值',
        type: 'line',
        data: trend.map((item) => item.unitNav),
        smooth: true,
        showSymbol: false,
        lineStyle: {
          width: 2,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#3B82F6' },
            { offset: 1, color: '#8B5CF6' },
          ]),
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.2)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' },
          ]),
        },
      },
    ],
    grid: { left: 60, right: 20, top: 30, bottom: 40 },
  })

  window.addEventListener('resize', () => chart.resize())
}

function formatPercent(val) {
  if (val == null) return '--'
  return (val >= 0 ? '+' : '') + val.toFixed(2) + '%'
}

function getReturnClass(val) {
  if (val == null) return ''
  return val >= 0 ? 'up' : 'down'
}
</script>

<style scoped>
.fund-detail {
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

/* Fund Header */
.fund-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 28px;
  margin-bottom: 20px;
}

.fund-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.fund-type-tag {
  padding: 4px 12px;
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.risk-tag {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.risk-tag.low {
  background: rgba(34, 197, 94, 0.1);
  color: #4ADE80;
}

.risk-tag.medium {
  background: rgba(245, 158, 11, 0.1);
  color: #FBBF24;
}

.risk-tag.high {
  background: rgba(239, 68, 68, 0.1);
  color: #F87171;
}

.fund-name {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 8px;
}

.fund-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-muted);
}

.fund-code {
  font-family: var(--font-mono);
  font-weight: 500;
  color: var(--color-primary);
}

.meta-sep {
  color: var(--color-text-dim);
}

.nav-display {
  text-align: right;
}

.nav-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-bottom: 4px;
}

.nav-value {
  font-size: 36px;
  font-weight: 700;
  font-family: var(--font-mono);
  color: var(--color-text);
}

.nav-date {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 4px;
}

/* Metrics Grid */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.metric-card {
  background: var(--color-card);
  border: 1px solid var(--color-glass);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  transition: all 0.2s ease;
}

.metric-card:hover {
  border-color: var(--color-glass-hover);
  transform: translateY(-2px);
}

.metric-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-bottom: 8px;
}

.metric-value {
  font-size: 18px;
  font-weight: 600;
  font-family: var(--font-mono);
  color: var(--color-text);
}

.metric-value.up { color: #22C55E; }
.metric-value.down { color: #EF4444; }

/* Sections */
.glass-card {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.chart-container {
  height: 400px;
}

/* Strategy */
.strategy-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--color-text-secondary);
  margin: 0;
}

/* Manager */
.manager-grid {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.manager-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.manager-avatar {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(139, 92, 246, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3B82F6;
}

.manager-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 4px;
}

.manager-company {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

.manager-stats {
  display: flex;
  gap: 24px;
}

.manager-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 11px;
  color: var(--color-text-muted);
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  font-family: var(--font-mono);
}

.manager-bio {
  font-size: 13px;
  line-height: 1.7;
  color: var(--color-text-secondary);
  margin: 0;
  padding-top: 16px;
  border-top: 1px solid var(--color-glass);
}

/* Holdings */
.holdings-grid {
  border: 1px solid var(--color-glass);
  border-radius: 10px;
  overflow: hidden;
}

.holding-header {
  display: grid;
  grid-template-columns: 120px 1fr 200px 150px;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-glass);
}

.holding-cell {
  padding: 12px 16px;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.holding-row {
  display: grid;
  grid-template-columns: 120px 1fr 200px 150px;
  border-bottom: 1px solid var(--color-glass);
  transition: background 0.15s ease;
}

.holding-row:last-child {
  border-bottom: none;
}

.holding-row:hover {
  background: rgba(255, 255, 255, 0.02);
}

.holding-row .holding-cell {
  font-size: 13px;
  color: var(--color-text);
}

.stock-code {
  font-family: var(--font-mono);
  color: var(--color-primary);
}

.ratio-bar {
  width: 80px;
  height: 4px;
  background: var(--color-surface);
  border-radius: 2px;
  overflow: hidden;
  display: inline-block;
  vertical-align: middle;
  margin-right: 8px;
}

.ratio-fill {
  height: 100%;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6);
  border-radius: 2px;
}

.ratio-value {
  font-family: var(--font-mono);
  font-size: 12px;
}

.text-success { color: #22C55E; }
</style>
