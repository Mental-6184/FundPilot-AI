<template>
  <div class="layout-wrapper">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <!-- Logo -->
      <div class="sidebar-logo" @click="router.push('/')">
        <div class="logo-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 3V21H21" stroke="url(#logoGrad)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M7 16L11 10L15 14L19 8" stroke="url(#logoGrad)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="19" cy="8" r="2" fill="url(#logoGrad)"/>
            <defs>
              <linearGradient id="logoGrad" x1="3" y1="3" x2="21" y2="21">
                <stop stop-color="#3B82F6"/>
                <stop offset="1" stop-color="#8B5CF6"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <transition name="fade">
          <span v-show="!isCollapse" class="logo-text">FundPilot</span>
        </transition>
      </div>

      <!-- Navigation -->
      <nav class="sidebar-nav">
        <div
          v-for="item in menuItems"
          :key="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          @click="router.push(item.path)"
        >
          <div class="nav-icon">
            <component :is="item.icon" />
          </div>
          <transition name="fade">
            <span v-show="!isCollapse" class="nav-label">{{ item.label }}</span>
          </transition>
          <transition name="fade">
            <span v-show="!isCollapse && item.badge" class="nav-badge">{{ item.badge }}</span>
          </transition>
        </div>
      </nav>

      <!-- Collapse Toggle -->
      <div class="sidebar-footer">
        <div class="collapse-toggle" @click="isCollapse = !isCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <transition name="fade">
            <span v-show="!isCollapse" class="collapse-text">收起菜单</span>
          </transition>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-wrapper">
      <!-- Top Header -->
      <header class="top-header">
        <div class="header-left">
          <div class="breadcrumb-area">
            <span class="breadcrumb-home" @click="router.push('/dashboard')">首页</span>
            <span v-if="route.meta.title && !route.meta.hideMenu" class="breadcrumb-sep">/</span>
            <span v-if="route.meta.title && !route.meta.hideMenu" class="breadcrumb-current">{{ route.meta.title }}</span>
          </div>
        </div>

        <div class="header-right">
          <!-- AI Quick Entry -->
          <div class="header-action ai-entry" @click="router.push('/advisor')" title="AI 投顾">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
              <path d="M16 14h.01"/>
              <path d="M8 14h.01"/>
              <path d="M12 16v4"/>
              <path d="M8 20h8"/>
              <path d="M6 10a6 6 0 0 0 12 0"/>
            </svg>
          </div>

          <!-- Notifications -->
          <div class="header-action" title="通知">
            <el-badge :value="0" :hidden="true" :max="99">
              <el-icon :size="18"><Bell /></el-icon>
            </el-badge>
          </div>

          <!-- User Avatar -->
          <el-dropdown trigger="click">
            <div class="user-avatar-area">
              <div class="user-avatar">
                <el-icon :size="16"><User /></el-icon>
              </div>
              <span class="user-name">{{ userStore.username }}</span>
              <el-icon :size="12" class="user-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/user')">
                  <el-icon><User /></el-icon>用户中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- Page Content -->
      <main class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const menuItems = [
  { path: '/dashboard', label: '首页', icon: 'Odometer' },
  { path: '/fund', label: '基金搜索', icon: 'Search' },
  { path: '/compare', label: '基金对比', icon: 'Histogram' },
  { path: '/portfolio', label: '我的组合', icon: 'Briefcase' },
  { path: '/advisor', label: 'AI 投顾', icon: 'ChatDotRound' },
  { path: '/user', label: '用户中心', icon: 'User' },
]

onMounted(() => {
  userStore.fetchUserInfo()
})

function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
/* Layout Wrapper */
.layout-wrapper {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: var(--color-bg);
}

/* ============================================
   Sidebar
   ============================================ */
.sidebar {
  width: 240px;
  height: 100vh;
  background: var(--color-sidebar);
  border-right: 1px solid var(--color-glass);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: var(--z-sidebar);
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 72px;
}

/* Logo */
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  cursor: pointer;
  border-bottom: 1px solid var(--color-glass);
  flex-shrink: 0;
}

.logo-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 10px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin: 2px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--color-text-secondary);
  position: relative;
  gap: 12px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--color-text);
}

.nav-item.active {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.15) 0%, transparent 100%);
  color: #3B82F6;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #3B82F6;
  border-radius: 0 3px 3px 0;
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.6);
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-label {
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.nav-badge {
  margin-left: auto;
  background: var(--color-primary);
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 600;
}

/* Sidebar Footer */
.sidebar-footer {
  padding: 12px 0;
  border-top: 1px solid var(--color-glass);
  flex-shrink: 0;
}

.collapse-toggle {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin: 0 12px;
  border-radius: 10px;
  cursor: pointer;
  color: var(--color-text-muted);
  transition: all 0.2s ease;
  gap: 12px;
}

.collapse-toggle:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--color-text-secondary);
}

.collapse-text {
  font-size: 13px;
  white-space: nowrap;
}

/* ============================================
   Main Wrapper
   ============================================ */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

/* ============================================
   Top Header
   ============================================ */
.top-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid var(--color-glass);
  background: rgba(11, 18, 32, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  flex-shrink: 0;
  z-index: var(--z-header);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.breadcrumb-area {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-home {
  color: var(--color-text-muted);
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-home:hover {
  color: var(--color-text-secondary);
}

.breadcrumb-sep {
  color: var(--color-text-dim);
}

.breadcrumb-current {
  color: var(--color-text-secondary);
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-action {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  color: var(--color-text-secondary);
  transition: all 0.2s ease;
}

.header-action:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--color-text);
}

.ai-entry {
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
}

.ai-entry:hover {
  background: rgba(59, 130, 246, 0.2);
}

.user-avatar-area {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-avatar-area:hover {
  background: rgba(255, 255, 255, 0.06);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-name {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.user-arrow {
  color: var(--color-text-muted);
}

/* ============================================
   Page Content
   ============================================ */
.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: var(--color-bg);
}

/* ============================================
   Transitions
   ============================================ */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.page-enter-active {
  animation: pageIn 0.3s ease-out;
}

.page-leave-active {
  animation: pageOut 0.2s ease-in;
}

@keyframes pageIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pageOut {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-8px);
  }
}
</style>
