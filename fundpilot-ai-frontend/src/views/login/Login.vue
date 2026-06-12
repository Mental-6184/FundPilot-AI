<template>
  <div class="login-page">
    <!-- Background Effects -->
    <div class="bg-effects">
      <div class="bg-gradient-1"></div>
      <div class="bg-gradient-2"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- Login Card -->
    <div class="login-card animate-fade-in-up">
      <!-- Logo -->
      <div class="login-logo">
        <div class="logo-icon-wrapper">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 3V21H21" stroke="url(#loginLogoGrad)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M7 16L11 10L15 14L19 8" stroke="url(#loginLogoGrad)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="19" cy="8" r="2" fill="url(#loginLogoGrad)"/>
            <defs>
              <linearGradient id="loginLogoGrad" x1="3" y1="3" x2="21" y2="21">
                <stop stop-color="#3B82F6"/>
                <stop offset="1" stop-color="#8B5CF6"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="login-title">FundPilot AI</h1>
        <p class="login-subtitle">智能基金投资分析平台</p>
      </div>

      <!-- Tabs -->
      <div class="login-tabs">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'login' }"
          @click="activeTab = 'login'"
        >
          登录
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'register' }"
          @click="activeTab = 'register'"
        >
          注册
        </button>
      </div>

      <!-- Login Form -->
      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <input
              v-model="loginForm.username"
              type="text"
              placeholder="请输入用户名"
              class="form-input"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              class="form-input"
            />
          </div>
        </div>

        <button
          type="submit"
          class="submit-btn"
          :disabled="loading"
        >
          <span v-if="loading" class="btn-loader"></span>
          <span v-else>登录</span>
        </button>
      </form>

      <!-- Register Form -->
      <form v-else @submit.prevent="handleRegister" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <input
              v-model="registerForm.username"
              type="text"
              placeholder="至少4个字符"
              class="form-input"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
              v-model="registerForm.password"
              type="password"
              placeholder="至少6个字符"
              class="form-input"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">确认密码</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="再次输入密码"
              class="form-input"
            />
          </div>
        </div>

        <button
          type="submit"
          class="submit-btn"
          :disabled="loading"
        >
          <span v-if="loading" class="btn-loader"></span>
          <span v-else>注册</span>
        </button>
      </form>

      <!-- Footer -->
      <div class="login-footer">
        <p>AI驱动的智能投资分析平台</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const activeTab = ref('login')

const loginForm = reactive({
  username: '',
  password: '',
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setToken(res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    const msg = e.response?.data?.message || '登录失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  if (registerForm.username.length < 4) {
    ElMessage.warning('用户名至少4个字符')
    return
  }
  if (registerForm.password.length < 6) {
    ElMessage.warning('密码至少6个字符')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  loading.value = true
  try {
    await register({
      username: registerForm.username,
      password: registerForm.password,
    })
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
    loginForm.password = registerForm.password
  } catch (e) {
    const msg = e.response?.data?.message || '注册失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0B1220;
  position: relative;
  overflow: hidden;
}

/* Background Effects */
.bg-effects {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-gradient-1 {
  position: absolute;
  top: -20%;
  right: -10%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.bg-gradient-2 {
  position: absolute;
  bottom: -20%;
  left: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
  background-size: 60px 60px;
}

/* Login Card */
.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(17, 24, 39, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  position: relative;
  z-index: 10;
}

/* Logo */
.login-logo {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon-wrapper {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #94A3B8;
  margin: 0;
}

/* Tabs */
.login-tabs {
  display: flex;
  gap: 4px;
  padding: 4px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 12px;
  margin-bottom: 24px;
}

.tab-btn {
  flex: 1;
  padding: 10px;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #94A3B8;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.tab-btn:hover {
  color: #F8FAFC;
}

.tab-btn.active {
  background: rgba(59, 130, 246, 0.15);
  color: #3B82F6;
}

/* Form */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: #94A3B8;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.input-wrapper:focus-within {
  border-color: #3B82F6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-icon {
  color: #64748B;
  font-size: 18px;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  height: 48px;
  background: transparent;
  border: none;
  outline: none;
  font-size: 14px;
  color: #F8FAFC;
  font-family: inherit;
}

.form-input::placeholder {
  color: #475569;
}

/* Submit Button */
.submit-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #60A5FA 0%, #3B82F6 100%);
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-loader {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Footer */
.login-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.login-footer p {
  font-size: 12px;
  color: #475569;
  margin: 0;
}
</style>
