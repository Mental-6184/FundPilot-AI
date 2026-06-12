<template>
  <div class="fund-list">
    <!-- Search Panel -->
    <div class="search-panel glass-card animate-fade-in-up">
      <div class="search-header">
        <h3 class="search-title">
          <el-icon><Search /></el-icon>
          <span>基金搜索</span>
        </h3>
        <p class="search-desc">输入基金代码、名称或选择筛选条件</p>
      </div>
      <div class="search-form">
        <div class="search-input-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="queryParams.keyword"
            type="text"
            placeholder="搜索基金代码或名称..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <div class="filter-item">
            <label class="filter-label">基金类型</label>
            <el-select v-model="queryParams.fundType" placeholder="全部类型" clearable size="default">
              <el-option label="股票型" value="EQUITY" />
              <el-option label="债券型" value="BOND" />
              <el-option label="混合型" value="HYBRID" />
              <el-option label="货币型" value="MONEY" />
              <el-option label="指数型" value="INDEX" />
              <el-option label="QDII" value="QDII" />
            </el-select>
          </div>
          <div class="filter-item">
            <label class="filter-label">风险等级</label>
            <el-select v-model="queryParams.riskLevel" placeholder="全部风险" clearable size="default">
              <el-option label="低风险" value="LOW" />
              <el-option label="中低风险" value="MEDIUM_LOW" />
              <el-option label="中风险" value="MEDIUM" />
              <el-option label="中高风险" value="MEDIUM_HIGH" />
              <el-option label="高风险" value="HIGH" />
            </el-select>
          </div>
          <button class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Results -->
    <div class="results-panel glass-card" style="margin-top: 16px">
      <div class="results-header">
        <h3 class="results-title">
          <span>搜索结果</span>
          <span class="results-count" v-if="fundStore.fundList.length">
            共 {{ fundStore.fundList.length }} 只基金
          </span>
        </h3>
      </div>

      <!-- Data Grid -->
      <div class="data-grid" v-loading="fundStore.loading">
        <!-- Table Header -->
        <div class="grid-header">
          <div class="grid-cell code">基金代码</div>
          <div class="grid-cell name">基金名称</div>
          <div class="grid-cell type">类型</div>
          <div class="grid-cell risk">风险等级</div>
          <div class="grid-cell nav">最新净值</div>
          <div class="grid-cell change">日涨跌幅</div>
          <div class="grid-cell action">操作</div>
        </div>

        <!-- Table Body -->
        <div
          v-for="fund in fundStore.fundList"
          :key="fund.fundCode"
          class="grid-row"
          @click="goDetail(fund.fundCode)"
        >
          <div class="grid-cell code">
            <span class="fund-code">{{ fund.fundCode }}</span>
          </div>
          <div class="grid-cell name">
            <span class="fund-name">{{ fund.fundName }}</span>
          </div>
          <div class="grid-cell type">
            <span class="type-tag">{{ fund.fundType }}</span>
          </div>
          <div class="grid-cell risk">
            <span class="risk-tag" :class="getRiskClass(fund.riskLevel)">{{ fund.riskLevel }}</span>
          </div>
          <div class="grid-cell nav">
            <span class="nav-value">{{ fund.latestNav?.toFixed(4) || '--' }}</span>
          </div>
          <div class="grid-cell change">
            <span class="change-value" :class="fund.dailyReturn >= 0 ? 'up' : 'down'">
              {{ fund.dailyReturn != null ? (fund.dailyReturn >= 0 ? '+' : '') + fund.dailyReturn.toFixed(2) + '%' : '--' }}
            </span>
          </div>
          <div class="grid-cell action">
            <button class="detail-btn" @click.stop="goDetail(fund.fundCode)">
              详情
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!fundStore.loading && !fundStore.fundList.length" class="empty-state">
          <div class="empty-icon">
            <el-icon :size="48"><Search /></el-icon>
          </div>
          <p class="empty-title">暂无搜索结果</p>
          <p class="empty-desc">尝试调整搜索条件或筛选器</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFundStore } from '@/stores/fund'

const router = useRouter()
const fundStore = useFundStore()

const queryParams = reactive({
  keyword: '',
  fundType: '',
  riskLevel: '',
  pageNum: 1,
  pageSize: 20,
})

onMounted(() => {
  fundStore.fetchFundList(queryParams)
})

function handleSearch() {
  queryParams.pageNum = 1
  fundStore.fetchFundList(queryParams)
}

function goDetail(fundCode) {
  router.push(`/fund/${fundCode}`)
}

function getRiskClass(riskLevel) {
  const map = {
    LOW: 'low',
    MEDIUM_LOW: 'low',
    MEDIUM: 'medium',
    MEDIUM_HIGH: 'high',
    HIGH: 'high',
  }
  return map[riskLevel] || 'default'
}
</script>

<style scoped>
.fund-list {
  max-width: 1400px;
  margin: 0 auto;
}

/* Search Panel */
.search-panel {
  padding: 24px;
}

.search-header {
  margin-bottom: 20px;
}

.search-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.search-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  background: var(--color-surface);
  border: 1px solid var(--color-glass);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.search-input-wrapper:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-icon {
  color: var(--color-text-muted);
  font-size: 18px;
}

.search-input {
  flex: 1;
  height: 48px;
  background: transparent;
  border: none;
  outline: none;
  font-size: 15px;
  color: var(--color-text);
  font-family: inherit;
}

.search-input::placeholder {
  color: var(--color-text-dim);
}

.filter-group {
  display: flex;
  gap: 16px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 180px;
}

.filter-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-muted);
}

.search-btn {
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

.search-btn:hover {
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

/* Results Panel */
.results-panel {
  padding: 24px;
}

.results-header {
  margin-bottom: 20px;
}

.results-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

.results-count {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-muted);
  background: var(--color-surface);
  padding: 4px 10px;
  border-radius: 6px;
}

/* Data Grid */
.data-grid {
  border: 1px solid var(--color-glass);
  border-radius: 12px;
  overflow: hidden;
}

.grid-header {
  display: grid;
  grid-template-columns: 120px 1fr 100px 100px 120px 120px 100px;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-glass);
}

.grid-cell {
  padding: 14px 16px;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
}

.grid-row {
  display: grid;
  grid-template-columns: 120px 1fr 100px 100px 120px 120px 100px;
  border-bottom: 1px solid var(--color-glass);
  cursor: pointer;
  transition: background 0.15s ease;
}

.grid-row:last-child {
  border-bottom: none;
}

.grid-row:hover {
  background: rgba(255, 255, 255, 0.03);
}

.grid-row .grid-cell {
  padding: 16px;
  font-size: 13px;
  color: var(--color-text);
}

.fund-code {
  font-family: var(--font-mono);
  font-weight: 500;
  color: var(--color-primary);
}

.fund-name {
  font-weight: 500;
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

.nav-value {
  font-family: var(--font-mono);
  font-weight: 500;
}

.change-value {
  font-family: var(--font-mono);
  font-weight: 600;
}

.change-value.up {
  color: #22C55E;
}

.change-value.down {
  color: #EF4444;
}

.detail-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(59, 130, 246, 0.1);
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  color: #3B82F6;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.detail-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

/* Empty State */
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: var(--color-surface);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-muted);
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}
</style>
