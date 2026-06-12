<template>
  <div class="portfolio-page">
    <!-- Page Header -->
    <div class="page-header animate-fade-in-up">
      <div>
        <h1 class="page-title">我的组合</h1>
        <p class="page-desc">管理您的投资组合，跟踪收益表现</p>
      </div>
      <button class="create-btn" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        <span>创建组合</span>
      </button>
    </div>

    <!-- Portfolio Grid -->
    <div class="portfolio-grid stagger-children">
      <div
        v-for="item in portfolioStore.portfolios"
        :key="item.id"
        class="portfolio-card glass-card"
      >
        <div class="card-header">
          <div class="card-icon">
            <el-icon :size="20"><Briefcase /></el-icon>
          </div>
          <button class="delete-btn" @click="handleDelete(item.id)">
            <el-icon><Delete /></el-icon>
          </button>
        </div>
        <h3 class="card-title">{{ item.portfolioName }}</h3>
        <p class="card-desc">{{ item.description || '暂无描述' }}</p>
        <div class="card-meta">
          <div class="meta-item">
            <el-icon><List /></el-icon>
            <span>{{ item.funds?.length || 0 }} 只基金</span>
          </div>
          <div class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>{{ item.updateTime }}</span>
          </div>
        </div>
        <button class="view-btn" @click="router.push(`/portfolio/${item.id}/analysis`)">
          <span>查看分析</span>
          <el-icon><ArrowRight /></el-icon>
        </button>
      </div>

      <!-- Empty State -->
      <div v-if="!portfolioStore.portfolios.length" class="empty-state">
        <div class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/>
            <path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/>
            <path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/>
          </svg>
        </div>
        <h3 class="empty-title">还没有投资组合</h3>
        <p class="empty-desc">创建您的第一个投资组合，开始跟踪基金表现</p>
        <button class="create-btn" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          <span>创建组合</span>
        </button>
      </div>
    </div>

    <!-- Create Dialog -->
    <el-dialog v-model="showCreateDialog" title="创建组合" width="500px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="组合名称" required>
          <el-input v-model="createForm.portfolioName" placeholder="请输入组合名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="基金代码" required>
          <el-select
            v-model="createForm.fundCodes"
            multiple
            filterable
            allow-create
            placeholder="输入基金代码后回车"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePortfolioStore } from '@/stores/portfolio'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const portfolioStore = usePortfolioStore()
const showCreateDialog = ref(false)
const createForm = reactive({
  portfolioName: '',
  description: '',
  fundCodes: [],
})

const userId = 1

onMounted(() => {
  portfolioStore.fetchPortfolios(userId)
})

async function handleCreate() {
  if (!createForm.portfolioName) {
    ElMessage.warning('请输入组合名称')
    return
  }
  if (!createForm.fundCodes.length) {
    ElMessage.warning('请至少添加一只基金')
    return
  }
  await portfolioStore.addPortfolio(userId, { ...createForm })
  showCreateDialog.value = false
  ElMessage.success('创建成功')
  createForm.portfolioName = ''
  createForm.description = ''
  createForm.fundCodes = []
}

async function handleDelete(portfolioId) {
  await ElMessageBox.confirm('确定删除该组合？', '提示', { type: 'warning' })
  await portfolioStore.removePortfolio(userId, portfolioId)
  ElMessage.success('删除成功')
}
</script>

<style scoped>
.portfolio-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 4px;
}

.page-desc {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.create-btn:hover {
  box-shadow: 0 6px 24px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

/* Portfolio Grid */
.portfolio-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.portfolio-card {
  padding: 24px;
  transition: all 0.2s ease;
}

.portfolio-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3B82F6;
}

.delete-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: transparent;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0 0 16px;
  line-height: 1.5;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.view-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #3B82F6;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.view-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.3);
}

/* Empty State */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 24px;
  background: var(--color-surface);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-muted);
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0 0 24px;
}
</style>
