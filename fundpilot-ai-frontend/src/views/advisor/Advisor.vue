<template>
  <div class="advisor-page">
    <!-- Session Sidebar -->
    <aside class="session-sidebar">
      <div class="sidebar-header">
        <h3 class="sidebar-title">会话历史</h3>
        <button class="new-chat-btn" @click="chatStore.newSession()">
          <el-icon><Plus /></el-icon>
        </button>
      </div>
      <div class="session-list scrollbar-hide">
        <div
          v-for="(sid, idx) in chatStore.sessions"
          :key="sid"
          class="session-item"
          :class="{ active: chatStore.sessionId === sid }"
          @click="chatStore.switchSession(sid)"
        >
          <div class="session-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <span class="session-name">会话 {{ idx + 1 }}</span>
          <button class="session-delete" @click.stop="chatStore.removeSession(sid)">
            <el-icon><Delete /></el-icon>
          </button>
        </div>
        <div v-if="!chatStore.sessions.length" class="empty-sessions">
          <p>暂无会话</p>
          <p class="empty-hint">点击 + 开始新对话</p>
        </div>
      </div>
    </aside>

    <!-- Chat Main -->
    <div class="chat-main">
      <!-- Chat Header -->
      <div class="chat-header">
        <div class="header-info">
          <div class="ai-avatar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
              <path d="M16 14h.01"/>
              <path d="M8 14h.01"/>
              <path d="M12 16v4"/>
              <path d="M8 20h8"/>
            </svg>
          </div>
          <div>
            <h3 class="header-title">FundPilot AI</h3>
            <span class="header-status">
              <span class="status-dot"></span>
              智能基金投资顾问
            </span>
          </div>
        </div>
        <button class="clear-btn" @click="chatStore.clearMessages()">
          <el-icon><Delete /></el-icon>
          <span>清空</span>
        </button>
      </div>

      <!-- Messages -->
      <div class="message-list scrollbar-hide" ref="messageListRef">
        <!-- Welcome -->
        <div v-if="!chatStore.messages.length" class="welcome-screen">
          <div class="welcome-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="url(#welcomeGrad)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
              <path d="M16 14h.01"/>
              <path d="M8 14h.01"/>
              <path d="M12 16v4"/>
              <path d="M8 20h8"/>
              <defs>
                <linearGradient id="welcomeGrad" x1="4" y1="2" x2="20" y2="22">
                  <stop stop-color="#3B82F6"/>
                  <stop offset="1" stop-color="#8B5CF6"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2 class="welcome-title">FundPilot AI 投顾助手</h2>
          <p class="welcome-desc">我是您的智能基金投资顾问，可以帮您分析基金、对比基金、诊断组合、推荐基金</p>
          <div class="suggestion-grid">
            <div
              v-for="item in suggestions"
              :key="item.text"
              class="suggestion-card"
              @click="sendSuggestion(item.text)"
            >
              <div class="suggestion-icon" :style="{ background: item.bg }">
                <el-icon :size="18" :style="{ color: item.color }">
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <span class="suggestion-text">{{ item.label }}</span>
            </div>
          </div>
        </div>

        <!-- Messages -->
        <div
          v-for="(msg, index) in chatStore.messages"
          :key="index"
          class="message-item"
          :class="msg.role"
        >
          <!-- User Avatar -->
          <div v-if="msg.role === 'USER'" class="msg-avatar user">
            <el-icon><User /></el-icon>
          </div>
          <!-- AI Avatar -->
          <div v-else class="msg-avatar ai">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
              <path d="M16 14h.01"/>
              <path d="M8 14h.01"/>
              <path d="M12 16v4"/>
              <path d="M8 20h8"/>
            </svg>
          </div>

          <!-- Message Content -->
          <div class="msg-content">
            <div class="msg-bubble" :class="msg.role">
              <div class="msg-text" v-html="renderMarkdown(msg.content)"></div>
            </div>
            <div class="msg-time">{{ formatTime(msg.timestamp) }}</div>
          </div>
        </div>

        <!-- Typing Indicator -->
        <div v-if="chatStore.loading" class="message-item ASSISTANT">
          <div class="msg-avatar ai">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 1 4 4v1a4 4 0 0 1-8 0V6a4 4 0 0 1 4-4z"/>
              <path d="M16 14h.01"/>
              <path d="M8 14h.01"/>
              <path d="M12 16v4"/>
              <path d="M8 20h8"/>
            </svg>
          </div>
          <div class="msg-content">
            <div class="msg-bubble ASSISTANT">
              <div class="typing-indicator">
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-area">
        <div class="input-wrapper">
          <textarea
            v-model="inputMessage"
            placeholder="输入您的问题，如：分析005827、比较005827和163406、推荐低风险基金..."
            class="chat-input"
            rows="1"
            @keydown.enter.exact.prevent="handleSend"
            @input="autoResize"
            ref="inputRef"
          ></textarea>
          <button
            class="send-btn"
            :disabled="!inputMessage.trim() || chatStore.loading"
            @click="handleSend"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="22" y1="2" x2="11" y2="13"/>
              <polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
          </button>
        </div>
        <p class="input-hint">Enter 发送，Shift + Enter 换行</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()
const inputMessage = ref('')
const messageListRef = ref(null)
const inputRef = ref(null)

const suggestions = [
  { label: '分析基金 005827', text: '帮我分析一下基金 005827', icon: 'TrendCharts', color: '#3B82F6', bg: 'rgba(59, 130, 246, 0.1)' },
  { label: '对比基金', text: '对比 005827 和 163406 两只基金', icon: 'Histogram', color: '#8B5CF6', bg: 'rgba(139, 92, 246, 0.1)' },
  { label: '推荐低风险基金', text: '推荐几只低风险的债券基金', icon: 'Present', color: '#22C55E', bg: 'rgba(34, 197, 94, 0.1)' },
  { label: '诊断我的组合', text: '帮我分析一下我的投资组合', icon: 'DataAnalysis', color: '#F59E0B', bg: 'rgba(245, 158, 11, 0.1)' },
]

onMounted(() => {
  chatStore.fetchSessions()
})

async function handleSend() {
  const msg = inputMessage.value.trim()
  if (!msg) return
  inputMessage.value = ''
  await chatStore.sendMessage(msg)
  scrollToBottom()
}

function sendSuggestion(text) {
  inputMessage.value = text
  handleSend()
}

function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

function autoResize() {
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
    inputRef.value.style.height = Math.min(inputRef.value.scrollHeight, 120) + 'px'
  }
}

function renderMarkdown(text) {
  if (!text) return ''
  return text
    .replace(/### (.*)/g, '<h4>$1</h4>')
    .replace(/## (.*)/g, '<h3>$1</h3>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br/>')
}

function formatTime(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

watch(() => chatStore.messages.length, scrollToBottom)
</script>

<style scoped>
.advisor-page {
  display: flex;
  height: calc(100vh - 104px);
  gap: 0;
  margin: -24px;
}

/* ============================================
   Session Sidebar
   ============================================ */
.session-sidebar {
  width: 260px;
  background: var(--color-sidebar);
  border-right: 1px solid var(--color-glass);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 16px;
  border-bottom: 1px solid var(--color-glass);
}

.sidebar-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

.new-chat-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(59, 130, 246, 0.1);
  border: none;
  color: #3B82F6;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.new-chat-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--color-text-secondary);
}

.session-item:hover {
  background: rgba(255, 255, 255, 0.04);
  color: var(--color-text);
}

.session-item.active {
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
}

.session-icon {
  width: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.session-name {
  flex: 1;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-delete {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: transparent;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
}

.session-item:hover .session-delete {
  opacity: 1;
}

.session-delete:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.empty-sessions {
  text-align: center;
  padding: 40px 16px;
}

.empty-sessions p {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

.empty-hint {
  font-size: 11px !important;
  margin-top: 4px !important;
}

/* ============================================
   Chat Main
   ============================================ */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: var(--color-bg);
}

/* Chat Header */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid var(--color-glass);
  background: rgba(11, 18, 32, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  flex-shrink: 0;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(139, 92, 246, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3B82F6;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

.header-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--color-text-muted);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #22C55E;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.clear-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: transparent;
  border: 1px solid var(--color-glass);
  border-radius: 8px;
  font-size: 12px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.clear-btn:hover {
  border-color: var(--color-glass-hover);
  color: var(--color-text-secondary);
}

/* Messages */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

/* Welcome Screen */
.welcome-screen {
  text-align: center;
  padding: 60px 20px;
  max-width: 600px;
  margin: 0 auto;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: rgba(59, 130, 246, 0.08);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px;
}

.welcome-desc {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0 0 32px;
  line-height: 1.6;
}

.suggestion-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.suggestion-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--color-card);
  border: 1px solid var(--color-glass);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.suggestion-card:hover {
  border-color: var(--color-glass-hover);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.suggestion-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.suggestion-text {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* Message Items */
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  max-width: 800px;
}

.message-item.USER {
  margin-left: auto;
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.msg-avatar.user {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: white;
}

.msg-avatar.ai {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(139, 92, 246, 0.2));
  color: #3B82F6;
}

.msg-content {
  max-width: 70%;
  min-width: 0;
}

.msg-bubble {
  padding: 14px 18px;
  border-radius: 16px;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-word;
}

.msg-bubble.USER {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: white;
  border-bottom-right-radius: 4px;
}

.msg-bubble.ASSISTANT {
  background: var(--color-card);
  color: var(--color-text);
  border: 1px solid var(--color-glass);
  border-bottom-left-radius: 4px;
}

.msg-text :deep(h3) {
  font-size: 15px;
  font-weight: 600;
  margin: 12px 0 6px;
  color: var(--color-text);
}

.msg-text :deep(h4) {
  font-size: 14px;
  font-weight: 600;
  margin: 10px 0 4px;
  color: var(--color-text);
}

.msg-text :deep(strong) {
  font-weight: 600;
  color: var(--color-text);
}

.msg-time {
  font-size: 11px;
  color: var(--color-text-dim);
  margin-top: 6px;
}

.message-item.USER .msg-time {
  text-align: right;
}

/* Typing Indicator */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-dot {
  width: 6px;
  height: 6px;
  background: var(--color-text-muted);
  border-radius: 50%;
  animation: typingBounce 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typingBounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* Input Area */
.input-area {
  padding: 16px 24px;
  border-top: 1px solid var(--color-glass);
  background: rgba(11, 18, 32, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding: 12px 16px;
  background: var(--color-surface);
  border: 1px solid var(--color-glass);
  border-radius: 14px;
  transition: all 0.2s ease;
}

.input-wrapper:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.chat-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: 14px;
  color: var(--color-text);
  font-family: inherit;
  resize: none;
  max-height: 120px;
  line-height: 1.5;
}

.chat-input::placeholder {
  color: var(--color-text-dim);
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.input-hint {
  font-size: 11px;
  color: var(--color-text-dim);
  margin: 8px 0 0;
  text-align: center;
}
</style>
