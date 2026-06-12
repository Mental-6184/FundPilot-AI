<template>
  <div class="dashboard">
    <!-- Welcome Section -->
    <div class="welcome-section animate-fade-in-up">
      <div class="welcome-text">
        <h1 class="welcome-title">
          <span class="greeting">{{ greeting }}</span>
          <span class="user-name">{{ userStore.username || '投资者' }}</span>
        </h1>
        <p class="welcome-sub">欢迎回到 FundPilot AI，您的智能投资分析平台</p>
      </div>
      <div class="welcome-actions">
        <button class="action-btn primary" @click="router.push('/advisor')">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
            <path d="M16 14h.01"/>
            <path d="M8 14h.01"/>
            <path d="M12 16v4"/>
            <path d="M8 20h8"/>
          </svg>
          <span>AI 投顾</span>
        </button>
        <button class="action-btn secondary" @click="router.push('/fund')">
          <el-icon><Search /></el-icon>
          <span>搜索基金</span>
        </button>
      </div>
    </div>

    <!-- Stat Cards -->
    <div class="stat-grid stagger-children">
      <div class="stat-card" v-for="(stat, index) in statCards" :key="stat.label">
        <div class="stat-header">
          <div class="stat-icon" :style="{ background: stat.iconBg }">
            <component :is="stat.icon" :style="{ color: stat.iconColor }" />
          </div>
          <div class="stat-trend" :class="stat.trendClass">
            <el-icon :size="12"><Top v-if="stat.trendUp" /><Bottom v-else /></el-icon>
            <span>{{ stat.trend }}</span>
          </div>
        </div>
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="content-grid">
      <!-- Market Overview -->
      <div class="market-panel glass-card">
        <div class="panel-header">
          <h3 class="panel-title">
            <el-icon><DataLine /></el-icon>
            <span>市场概览</span>
          </h3>
          <span class="panel-badge">实时</span>
        </div>
        <div class="market-list">
          <div class="market-item" v-for="item in marketData" :key="item.name">
            <div class="market-info">
              <span class="market-name">{{ item.name }}</span>
              <span class="market-code">{{ item.code }}</span>
            </div>
            <div class="market-data">
              <span class="market-value" :class="item.change >= 0 ? 'up' : 'down'">
                {{ item.value }}
              </span>
              <span class="market-change" :class="item.change >= 0 ? 'up' : 'down'">
                {{ item.change >= 0 ? '+' : '' }}{{ item.change }}%
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-panel glass-card">
        <div class="panel-header">
          <h3 class="panel-title">
            <el-icon><Operation /></el-icon>
            <span>快捷操作</span>
          </h3>
        </div>
        <div class="quick-grid">
          <div class="quick-item" @click="router.push('/fund')">
            <div class="quick-icon" style="background: rgba(59, 130, 246, 0.1)">
              <el-icon :size="24" style="color: #3B82F6"><Search /></el-icon>
            </div>
            <span class="quick-label">搜索基金</span>
            <span class="quick-desc">查找并分析基金</span>
          </div>
          <div class="quick-item" @click="router.push('/compare')">
            <div class="quick-icon" style="background: rgba(139, 92, 246, 0.1)">
              <el-icon :size="24" style="color: #8B5CF6"><Histogram /></el-icon>
            </div>
            <span class="quick-label">基金对比</span>
            <span class="quick-desc">多维度对比分析</span>
          </div>
          <div class="quick-item" @click="router.push('/portfolio')">
            <div class="quick-icon" style="background: rgba(34, 197, 94, 0.1)">
              <el-icon :size="24" style="color: #22C55E"><Briefcase /></el-icon>
            </div>
            <span class="quick-label">我的组合</span>
            <span class="quick-desc">管理投资组合</span>
          </div>
          <div class="quick-item" @click="router.push('/advisor')">
            <div class="quick-icon" style="background: rgba(245, 158, 11, 0.1)">
              <el-icon :size="24" style="color: #F59E0B"><ChatDotRound /></el-icon>
            </div>
            <span class="quick-label">AI 投顾</span>
            <span class="quick-desc">智能投资顾问</span>
          </div>
        </div>
      </div>

      <!-- AI Insight -->
      <div class="insight-panel glass-card">
        <div class="panel-header">
          <h3 class="panel-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
              <path d="M16 14h.01"/>
              <path d="M8 14h.01"/>
              <path d="M12 16v4"/>
              <path d="M8 20h8"/>
            </svg>
            <span>AI 洞察</span>
          </h3>
          <span class="panel-badge ai">AI</span>
        </div>
        <div class="insight-content">
          <div class="insight-item">
            <div class="insight-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <path d="M12 16v-4"/>
                <path d="M12 8h.01"/>
              </svg>
            </div>
            <div class="insight-text">
              <p class="insight-title">市场观点</p>
              <p class="insight-desc">当前市场处于震荡调整期，建议关注估值合理的成长型基金</p>
            </div>
          </div>
          <div class="insight-item">
            <div class="insight-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <line x1="12" y1="9" x2="12" y2="13"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
            </div>
            <div class="insight-text">
              <p class="insight-title">风险提醒</p>
              <p class="insight-desc">部分行业基金集中度较高，建议适当分散投资降低风险</p>
            </div>
          </div>
          <div class="insight-item">
            <div class="insight-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#22C55E" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
            </div>
            <div class="insight-text">
              <p class="insight-title">基金推荐</p>
              <p class="insight-desc">基于您的风险偏好，推荐关注均衡型混合基金</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const stats = reactive({
  fundCount: '--',
  typeCount: 6,
  portfolioCount: '--',
  chatCount: '--',
})

const statCards = [
  {
    label: '基金总数',
    value: stats.fundCount,
    icon: 'TrendCharts',
    iconBg: 'rgba(59, 130, 246, 0.1)',
    iconColor: '#3B82F6',
    trend: '+12',
    trendUp: true,
    trendClass: 'up',
  },
  {
    label: '基金类型',
    value: stats.typeCount,
    icon: 'List',
    iconBg: 'rgba(139, 92, 246, 0.1)',
    iconColor: '#8B5CF6',
    trend: '6类',
    trendUp: true,
    trendClass: 'neutral',
  },
  {
    label: '我的组合',
    value: stats.portfolioCount,
    icon: 'Briefcase',
    iconBg: 'rgba(34, 197, 94, 0.1)',
    iconColor: '#22C55E',
    trend: '+2',
    trendUp: true,
    trendClass: 'up',
  },
  {
    label: 'AI 对话',
    value: stats.chatCount,
    icon: 'ChatDotRound',
    iconBg: 'rgba(245, 158, 11, 0.1)',
    iconColor: '#F59E0B',
    trend: '+5',
    trendUp: true,
    trendClass: 'up',
  },
]

const marketData = [
  { name: '上证指数', code: 'SH000001', value: '3,368.07', change: 0.52 },
  { name: '深证成指', code: 'SZ399001', value: '11,023.45', change: 0.78 },
  { name: '创业板指', code: 'SZ399006', value: '2,178.90', change: -0.34 },
  { name: '沪深300', code: 'SH000300', value: '3,942.15', change: 0.45 },
  { name: '中证500', code: 'SH000905', value: '5,621.30', change: -0.12 },
]
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

/* Welcome Section */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 28px 32px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(139, 92, 246, 0.08) 100%);
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 20px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.greeting {
  color: var(--color-text-secondary);
}

.user-name {
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-sub {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

.welcome-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  font-family: inherit;
}

.action-btn.primary {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.action-btn.primary:hover {
  box-shadow: 0 6px 24px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

.action-btn.secondary {
  background: var(--color-surface);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-glass);
}

.action-btn.secondary:hover {
  border-color: var(--color-glass-hover);
  color: var(--color-text);
}

/* Stat Cards */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: var(--color-card);
  border: 1px solid var(--color-glass);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  border-color: var(--color-glass-hover);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-trend.up {
  background: rgba(34, 197, 94, 0.1);
  color: #4ADE80;
}

.stat-trend.down {
  background: rgba(239, 68, 68, 0.1);
  color: #F87171;
}

.stat-trend.neutral {
  background: var(--color-surface);
  color: var(--color-text-secondary);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text);
  font-family: var(--font-mono);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-muted);
}

/* Content Grid */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.insight-panel {
  grid-column: 1 / -1;
}

/* Panel Common */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

.panel-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 6px;
  background: var(--color-surface);
  color: var(--color-text-secondary);
}

.panel-badge.ai {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(139, 92, 246, 0.2));
  color: #8B5CF6;
}

/* Market Panel */
.market-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.market-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 10px;
  transition: background 0.2s ease;
}

.market-item:hover {
  background: rgba(255, 255, 255, 0.04);
}

.market-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.market-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.market-code {
  font-size: 11px;
  color: var(--color-text-muted);
  font-family: var(--font-mono);
}

.market-data {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.market-value {
  font-size: 16px;
  font-weight: 600;
  font-family: var(--font-mono);
}

.market-value.up { color: #22C55E; }
.market-value.down { color: #EF4444; }

.market-change {
  font-size: 12px;
  font-weight: 500;
  font-family: var(--font-mono);
}

.market-change.up { color: #4ADE80; }
.market-change.down { color: #F87171; }

/* Quick Actions */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

.quick-item:hover {
  background: rgba(255, 255, 255, 0.04);
  transform: translateY(-2px);
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.quick-desc {
  font-size: 11px;
  color: var(--color-text-muted);
}

/* Insight Panel */
.insight-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.insight-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  transition: background 0.2s ease;
}

.insight-item:hover {
  background: rgba(255, 255, 255, 0.04);
}

.insight-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.insight-text {
  flex: 1;
  min-width: 0;
}

.insight-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 4px;
}

.insight-desc {
  font-size: 12px;
  color: var(--color-text-muted);
  margin: 0;
  line-height: 1.5;
}

/* Responsive */
@media (max-width: 1200px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .insight-content {
    grid-template-columns: 1fr;
  }
}
</style>
