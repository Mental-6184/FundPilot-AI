<template>
  <div class="user-center">
    <div class="user-grid">
      <!-- Profile Card -->
      <div class="profile-card glass-card animate-fade-in-up">
        <div class="profile-header">
          <div class="profile-avatar">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="profile-info">
            <h2 class="profile-name">{{ userStore.username }}</h2>
            <p class="profile-id">ID: {{ userStore.userId }}</p>
          </div>
        </div>
        <div class="profile-details">
          <div class="detail-item">
            <span class="detail-label">用户名</span>
            <span class="detail-value">{{ userStore.userInfo?.username || '--' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">注册时间</span>
            <span class="detail-value">{{ userStore.userInfo?.createTime || '--' }}</span>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </button>
      </div>

      <!-- Stats & Actions -->
      <div class="stats-area">
        <!-- Stat Cards -->
        <div class="stat-grid stagger-children">
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(59, 130, 246, 0.1)">
              <el-icon :size="20" style="color: #3B82F6"><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ portfolioCount }}</div>
              <div class="stat-label">我的组合</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(139, 92, 246, 0.1)">
              <el-icon :size="20" style="color: #8B5CF6"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ chatCount }}</div>
              <div class="stat-label">AI 对话</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(34, 197, 94, 0.1)">
              <el-icon :size="20" style="color: #22C55E"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ reportCount }}</div>
              <div class="stat-label">分析报告</div>
            </div>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="actions-card glass-card">
          <h3 class="section-title">
            <el-icon><Operation /></el-icon>
            <span>快捷操作</span>
          </h3>
          <div class="action-grid">
            <div class="action-item" @click="router.push('/fund')">
              <div class="action-icon" style="background: rgba(59, 130, 246, 0.1)">
                <el-icon :size="24" style="color: #3B82F6"><Search /></el-icon>
              </div>
              <span class="action-label">搜索基金</span>
            </div>
            <div class="action-item" @click="router.push('/compare')">
              <div class="action-icon" style="background: rgba(139, 92, 246, 0.1)">
                <el-icon :size="24" style="color: #8B5CF6"><Histogram /></el-icon>
              </div>
              <span class="action-label">基金对比</span>
            </div>
            <div class="action-item" @click="router.push('/advisor')">
              <div class="action-icon" style="background: rgba(34, 197, 94, 0.1)">
                <el-icon :size="24" style="color: #22C55E"><ChatDotRound /></el-icon>
              </div>
              <span class="action-label">AI 投顾</span>
            </div>
          </div>
        </div>

        <!-- Recent Sessions -->
        <div class="sessions-card glass-card">
          <h3 class="section-title">
            <el-icon><Clock /></el-icon>
            <span>最近会话</span>
          </h3>
          <div class="session-list" v-if="recentSessions.length">
            <div
              v-for="session in recentSessions"
              :key="session"
              class="session-item"
              @click="goToChat(session)"
            >
              <div class="session-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <span class="session-id">{{ session }}</span>
              <button class="session-action">
                <span>继续对话</span>
                <el-icon><ArrowRight /></el-icon>
              </button>
            </div>
          </div>
          <div v-else class="empty-sessions">
            <p>暂无会话记录</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useChatStore } from '@/stores/chat'
import { usePortfolioStore } from '@/stores/portfolio'

const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()
const portfolioStore = usePortfolioStore()

const portfolioCount = computed(() => portfolioStore.portfolios.length)
const chatCount = computed(() => chatStore.sessions.length)
const reportCount = ref(0)
const recentSessions = computed(() => chatStore.sessions.slice(0, 5))

onMounted(async () => {
  await Promise.all([
    userStore.fetchUserInfo(),
    chatStore.fetchSessions(),
    portfolioStore.fetchPortfolios(),
  ])
})

function goToChat(sessionId) {
  chatStore.switchSession(sessionId)
  router.push('/advisor')
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.user-center {
  max-width: 1200px;
  margin: 0 auto;
}

.user-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
}

/* Profile Card */
.profile-card {
  padding: 28px;
  height: fit-content;
  position: sticky;
  top: 24px;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-glass);
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(139, 92, 246, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3B82F6;
  border: 2px solid rgba(59, 130, 246, 0.3);
}

.profile-info {
  text-align: center;
}

.profile-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 4px;
}

.profile-id {
  font-size: 12px;
  color: var(--color-text-muted);
  margin: 0;
  font-family: var(--font-mono);
}

.profile-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 8px;
}

.detail-label {
  font-size: 13px;
  color: var(--color-text-muted);
}

.detail-value {
  font-size: 13px;
  color: var(--color-text);
  font-weight: 500;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: rgba(239, 68, 68, 0.08);
  border: 1px solid rgba(239, 68, 68, 0.15);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #EF4444;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.25);
}

/* Stats Area */
.stats-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Stat Grid */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  background: var(--color-card);
  border: 1px solid var(--color-glass);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  border-color: var(--color-glass-hover);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  font-family: var(--font-mono);
}

.stat-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

/* Actions Card */
.actions-card {
  padding: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 16px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-item:hover {
  background: rgba(255, 255, 255, 0.04);
  transform: translateY(-2px);
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-secondary);
}

/* Sessions Card */
.sessions-card {
  padding: 24px;
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.session-item:hover {
  background: rgba(255, 255, 255, 0.04);
}

.session-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3B82F6;
  flex-shrink: 0;
}

.session-id {
  flex: 1;
  font-size: 12px;
  font-family: var(--font-mono);
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-action {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(59, 130, 246, 0.1);
  border: none;
  border-radius: 6px;
  font-size: 12px;
  color: #3B82F6;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.session-action:hover {
  background: rgba(59, 130, 246, 0.2);
}

.empty-sessions {
  text-align: center;
  padding: 24px;
}

.empty-sessions p {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .user-grid {
    grid-template-columns: 1fr;
  }

  .profile-card {
    position: static;
  }
}
</style>
