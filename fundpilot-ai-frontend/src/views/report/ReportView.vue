<template>
  <div class="report-view">
    <!-- Back Button -->
    <div class="back-btn" @click="router.back()">
      <el-icon><ArrowLeft /></el-icon>
      <span>返回</span>
    </div>

    <div class="report-card glass-card" v-loading="loading">
      <div class="report-header">
        <h1 class="report-title">{{ reportTitle }}</h1>
      </div>
      <div v-if="reportContent" class="report-content" v-html="renderReport"></div>
      <div v-else-if="!loading" class="empty-state">
        <div class="empty-icon">
          <el-icon :size="48"><Document /></el-icon>
        </div>
        <p class="empty-title">暂无报告内容</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFundReport, getCompareReport, getPortfolioReport, getRecommendReport } from '@/api/analytics'

const route = useRoute()
const router = useRouter()

const reportContent = ref('')
const loading = ref(false)

const reportType = computed(() => route.params.type)
const reportId = computed(() => route.params.id)

const reportTitle = computed(() => {
  const titles = {
    fund: '基金分析报告',
    compare: '基金对比报告',
    portfolio: '组合诊断报告',
    recommend: '基金推荐报告',
  }
  return titles[reportType.value] || '分析报告'
})

const renderReport = computed(() => {
  if (!reportContent.value) return ''
  return reportContent.value
    .replace(/^### (.*$)/gim, '<h4>$1</h4>')
    .replace(/^## (.*$)/gim, '<h3>$1</h3>')
    .replace(/^# (.*$)/gim, '<h2>$1</h2>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br/>')
    .replace(/^\s*[-*]\s(.*)/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>)/gs, '<ul>$1</ul>')
    .replace(/^\s*\d+\.\s(.*)/gm, '<li>$1</li>')
})

onMounted(async () => {
  loading.value = true
  try {
    let res
    switch (reportType.value) {
      case 'fund':
        res = await getFundReport(reportId.value)
        break
      case 'compare':
        res = await getCompareReport(reportId.value)
        break
      case 'portfolio':
        res = await getPortfolioReport(reportId.value)
        break
      case 'recommend':
        res = await getRecommendReport({ demand: reportId.value })
        break
    }
    reportContent.value = res?.data || ''
  } catch (error) {
    console.error('加载报告失败', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.report-view {
  max-width: 900px;
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

.report-card {
  padding: 32px;
}

.report-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-glass);
}

.report-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
}

.report-content {
  font-size: 14px;
  line-height: 1.8;
  color: var(--color-text-secondary);
}

.report-content :deep(h2) {
  font-size: 20px;
  margin: 24px 0 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #3B82F6;
  color: var(--color-text);
}

.report-content :deep(h3) {
  font-size: 17px;
  margin: 20px 0 8px;
  color: #3B82F6;
}

.report-content :deep(h4) {
  font-size: 15px;
  margin: 16px 0 6px;
  color: var(--color-text);
}

.report-content :deep(strong) {
  color: var(--color-text);
  font-weight: 600;
}

.report-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.report-content :deep(th),
.report-content :deep(td) {
  border: 1px solid var(--color-glass);
  padding: 10px 14px;
  text-align: left;
}

.report-content :deep(th) {
  background: var(--color-surface);
  font-weight: 600;
  color: var(--color-text-secondary);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.report-content :deep(td) {
  color: var(--color-text);
}

.report-content :deep(ul) {
  padding-left: 20px;
  margin: 8px 0;
}

.report-content :deep(li) {
  margin: 4px 0;
}

.report-content :deep(hr) {
  border: none;
  border-top: 1px solid var(--color-glass);
  margin: 20px 0;
}

.report-content :deep(blockquote) {
  border-left: 4px solid #3B82F6;
  padding: 12px 16px;
  margin: 16px 0;
  background: rgba(59, 130, 246, 0.08);
  color: var(--color-text-secondary);
  border-radius: 0 8px 8px 0;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 20px;
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
  margin: 0;
}
</style>
