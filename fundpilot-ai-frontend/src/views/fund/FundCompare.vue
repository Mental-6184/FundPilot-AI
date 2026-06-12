<template>
  <div class="fund-compare">
    <!-- Input Section -->
    <div class="compare-input glass-card animate-fade-in-up">
      <div class="input-header">
        <h3 class="input-title">
          <el-icon><Histogram /></el-icon>
          <span>基金对比</span>
        </h3>
        <p class="input-desc">选择至少2只基金进行多维度对比分析</p>
      </div>
      <div class="input-form">
        <div class="select-wrapper">
          <el-select
            v-model="selectedCodes"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="输入基金代码后回车（至少2只）"
            size="large"
          />
        </div>
        <div class="input-actions">
          <button
            class="compare-btn"
            :disabled="selectedCodes.length < 2 || compareStore.loading"
            @click="handleCompare"
          >
            <span v-if="compareStore.loading" class="btn-loader"></span>
            <el-icon v-else><Histogram /></el-icon>
            <span>{{ compareStore.loading ? '分析中...' : '开始对比' }}</span>
          </button>
          <button class="clear-btn" @click="handleClear">
            <span>清空</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Results -->
    <template v-if="compareData">
      <!-- Basic Info -->
      <div class="compare-section glass-card">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            <span>基本信息对比</span>
          </h3>
        </div>
        <div class="compare-grid">
          <div class="compare-row header">
            <div class="compare-cell label"></div>
            <div class="compare-cell" v-for="fund in compareData.funds" :key="fund.fundCode">
              <span class="fund-code">{{ fund.fundCode }}</span>
            </div>
          </div>
          <div class="compare-row">
            <div class="compare-cell label">基金名称</div>
            <div class="compare-cell" v-for="fund in compareData.funds" :key="fund.fundCode">
              {{ fund.fundName }}
            </div>
          </div>
          <div class="compare-row">
            <div class="compare-cell label">类型</div>
            <div class="compare-cell" v-for="fund in compareData.funds" :key="fund.fundCode">
              <span class="type-tag">{{ fund.fundType }}</span>
            </div>
          </div>
          <div class="compare-row">
            <div class="compare-cell label">风险等级</div>
            <div class="compare-cell" v-for="fund in compareData.funds" :key="fund.fundCode">
              <span class="risk-tag" :class="getRiskClass(fund.riskLevel)">{{ fund.riskLevel }}</span>
            </div>
          </div>
          <div class="compare-row">
            <div class="compare-cell label">规模(亿)</div>
            <div class="compare-cell" v-for="fund in compareData.funds" :key="fund.fundCode">
              <span class="mono">{{ fund.scale || '--' }}</span>
            </div>
          </div>
          <div class="compare-row">
            <div class="compare-cell label">最新净值</div>
            <div class="compare-cell" v-for="fund in compareData.funds" :key="fund.fundCode">
              <span class="mono">{{ fund.latestNav?.toFixed(4) || '--' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Return Comparison -->
      <div class="compare-section glass-card">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><TrendCharts /></el-icon>
            <span>收益对比</span>
          </h3>
        </div>
        <div ref="returnChartRef" class="chart-container"></div>
        <div class="return-table">
          <div class="return-row header">
            <div class="return-cell label">基金代码</div>
            <div class="return-cell">近1周</div>
            <div class="return-cell">近1月</div>
            <div class="return-cell">近3月</div>
            <div class="return-cell">近6月</div>
            <div class="return-cell">近1年</div>
          </div>
          <div class="return-row" v-for="fund in compareData.funds" :key="fund.fundCode">
            <div class="return-cell label">
              <span class="fund-code">{{ fund.fundCode }}</span>
            </div>
            <div class="return-cell">
              <span :class="getReturnClass(fund.return1w)">{{ formatPct(fund.return1w) }}</span>
            </div>
            <div class="return-cell">
              <span :class="getReturnClass(fund.return1m)">{{ formatPct(fund.return1m) }}</span>
            </div>
            <div class="return-cell">
              <span :class="getReturnClass(fund.return3m)">{{ formatPct(fund.return3m) }}</span>
            </div>
            <div class="return-cell">
              <span :class="getReturnClass(fund.return6m)">{{ formatPct(fund.return6m) }}</span>
            </div>
            <div class="return-cell">
              <span :class="getReturnClass(fund.return1y)">{{ formatPct(fund.return1y) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Risk Comparison -->
      <div class="compare-section glass-card">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Warning /></el-icon>
            <span>风险对比</span>
          </h3>
        </div>
        <div class="risk-grid">
          <div class="risk-row header">
            <div class="risk-cell label">基金代码</div>
            <div class="risk-cell">波动率</div>
            <div class="risk-cell">最大回撤</div>
            <div class="risk-cell">夏普比率</div>
            <div class="risk-cell">索提诺比率</div>
          </div>
          <div class="risk-row" v-for="fund in compareData.funds" :key="fund.fundCode">
            <div class="risk-cell label">
              <span class="fund-code">{{ fund.fundCode }}</span>
            </div>
            <div class="risk-cell">
              <span class="mono">{{ formatPct(fund.volatility) }}</span>
            </div>
            <div class="risk-cell">
              <span class="mono down">{{ formatPct(fund.maxDrawdown) }}</span>
            </div>
            <div class="risk-cell">
              <span class="mono">{{ fund.sharpeRatio?.toFixed(4) || '--' }}</span>
            </div>
            <div class="risk-cell">
              <span class="mono">{{ fund.sortinoRatio?.toFixed(4) || '--' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Generate Report -->
      <div class="report-section glass-card">
        <button class="report-btn" :loading="reportLoading" @click="generateReport">
          <el-icon><Document /></el-icon>
          <span>生成对比报告</span>
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useCompareStore } from '@/stores/compare'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { formatPercent, getReturnClass } from '@/utils/format'

const router = useRouter()
const compareStore = useCompareStore()

const selectedCodes = ref([])
const compareData = ref(null)
const returnChartRef = ref(null)
const reportLoading = ref(false)

async function handleCompare() {
  if (selectedCodes.value.length < 2) {
    ElMessage.warning('请至少选择2只基金')
    return
  }
  await compareStore.fetchCompare(selectedCodes.value)
  compareData.value = compareStore.compareResult
  nextTick(() => renderReturnChart())
}

function handleClear() {
  selectedCodes.value = []
  compareData.value = null
  compareStore.clear()
}

function renderReturnChart() {
  if (!returnChartRef.value || !compareData.value) return
  const chart = echarts.init(returnChartRef.value)
  const funds = compareData.value.funds
  const periods = ['近1周', '近1月', '近3月', '近6月', '近1年']
  const keys = ['return1w', 'return1m', 'return3m', 'return6m', 'return1y']

  const colors = ['#3B82F6', '#8B5CF6', '#22C55E', '#F59E0B', '#EF4444']

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
    },
    legend: {
      data: funds.map((f) => f.fundCode),
      textStyle: { color: '#94A3B8', fontSize: 12 },
      top: 0,
    },
    xAxis: {
      type: 'category',
      data: periods,
      axisLabel: { color: '#64748B', fontSize: 11 },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#64748B', fontSize: 11, formatter: '{value}%' },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.04)' } },
    },
    series: funds.map((f, i) => ({
      name: f.fundCode,
      type: 'bar',
      data: keys.map((k) => f[k] ?? 0),
      itemStyle: {
        color: colors[i % colors.length],
        borderRadius: [4, 4, 0, 0],
      },
      barMaxWidth: 32,
    })),
    grid: { left: 60, right: 20, top: 40, bottom: 30 },
  })
  window.addEventListener('resize', () => chart.resize())
}

async function generateReport() {
  reportLoading.value = true
  try {
    await compareStore.fetchReport(selectedCodes.value.join(','))
    router.push(`/report/compare/${selectedCodes.value.join(',')}`)
  } finally {
    reportLoading.value = false
  }
}

function formatPct(val) {
  return formatPercent(val)
}

function getRiskClass(risk) {
  const map = { LOW: 'low', MEDIUM_LOW: 'low', MEDIUM: 'medium', MEDIUM_HIGH: 'high', HIGH: 'high' }
  return map[risk] || 'default'
}
</script>

<style scoped>
.fund-compare {
  max-width: 1200px;
  margin: 0 auto;
}

/* Input Section */
.compare-input {
  padding: 28px;
  margin-bottom: 20px;
}

.input-header {
  margin-bottom: 20px;
}

.input-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.input-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

.input-form {
  display: flex;
  gap: 16px;
  align-items: flex-end;
}

.select-wrapper {
  flex: 1;
}

.input-actions {
  display: flex;
  gap: 12px;
}

.compare-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 24px;
  height: 40px;
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.compare-btn:hover:not(:disabled) {
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.compare-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-loader {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.clear-btn {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 40px;
  background: var(--color-surface);
  border: 1px solid var(--color-glass);
  border-radius: 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.clear-btn:hover {
  border-color: var(--color-glass-hover);
  color: var(--color-text);
}

/* Compare Sections */
.compare-section {
  padding: 24px;
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

/* Compare Grid */
.compare-grid,
.return-table,
.risk-grid {
  border: 1px solid var(--color-glass);
  border-radius: 10px;
  overflow: hidden;
}

.compare-row,
.return-row,
.risk-row {
  display: grid;
  border-bottom: 1px solid var(--color-glass);
}

.compare-row { grid-template-columns: 120px repeat(auto-fit, minmax(150px, 1fr)); }
.return-row { grid-template-columns: 100px repeat(5, 1fr); }
.risk-row { grid-template-columns: 100px repeat(4, 1fr); }

.compare-row:last-child,
.return-row:last-child,
.risk-row:last-child {
  border-bottom: none;
}

.compare-row.header,
.return-row.header,
.risk-row.header {
  background: var(--color-surface);
}

.compare-cell,
.return-cell,
.risk-cell {
  padding: 14px 16px;
  font-size: 13px;
  color: var(--color-text);
  display: flex;
  align-items: center;
}

.compare-cell.label,
.return-cell.label,
.risk-cell.label {
  font-weight: 600;
  color: var(--color-text-secondary);
  background: rgba(255, 255, 255, 0.02);
}

.fund-code {
  font-family: var(--font-mono);
  font-weight: 500;
  color: #3B82F6;
}

.type-tag {
  display: inline-block;
  padding: 4px 10px;
  background: var(--color-surface);
  border-radius: 6px;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.risk-tag {
  display: inline-block;
  padding: 4px 10px;
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

.mono {
  font-family: var(--font-mono);
}

.up { color: #22C55E; }
.down { color: #EF4444; }

.chart-container {
  height: 350px;
  margin-bottom: 20px;
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
