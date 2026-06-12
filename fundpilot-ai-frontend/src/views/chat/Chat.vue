<template>
  <div class="chat-page">
    <el-card class="chat-card">
      <template #header>
        <div class="chat-header">
          <span>AI 基金分析助手</span>
          <el-button type="info" link @click="chatStore.clearMessages()">
            <el-icon><Delete /></el-icon>清空对话
          </el-button>
        </div>
      </template>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div v-if="!chatStore.messages.length" class="welcome">
          <el-icon :size="48" color="#409eff"><ChatDotRound /></el-icon>
          <h3>欢迎使用 FundPilot AI</h3>
          <p>我可以帮你分析基金、对比基金、提供投资建议</p>
          <div class="suggestions">
            <el-button @click="sendSuggestion('帮我推荐几只低风险的债券基金')">推荐低风险基金</el-button>
            <el-button @click="sendSuggestion('对比 110011 和 000001 两只基金')">基金对比</el-button>
            <el-button @click="sendSuggestion('分析一下我的投资组合')">组合分析</el-button>
          </div>
        </div>

        <div v-for="(msg, index) in chatStore.messages" :key="index" class="message-item" :class="msg.role">
          <el-avatar v-if="msg.role === 'USER'" :size="36" icon="User" class="avatar" />
          <el-avatar v-else :size="36" class="avatar ai-avatar">
            <el-icon><Monitor /></el-icon>
          </el-avatar>
          <div class="message-content">
            <div class="message-bubble" :class="msg.role">
              <pre class="message-text">{{ msg.content }}</pre>
            </div>
          </div>
        </div>

        <div v-if="chatStore.loading" class="message-item ASSISTANT">
          <el-avatar :size="36" class="avatar ai-avatar">
            <el-icon><Monitor /></el-icon>
          </el-avatar>
          <div class="message-content">
            <div class="message-bubble ASSISTANT">
              <span class="typing">思考中...</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          placeholder="输入你的问题，如：帮我分析基金 110011"
          :rows="2"
          type="textarea"
          resize="none"
          @keydown.enter.ctrl="handleSend"
        />
        <el-button type="primary" :disabled="!inputMessage.trim() || chatStore.loading" @click="handleSend">
          发送 (Ctrl+Enter)
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()
const inputMessage = ref('')
const messageListRef = ref(null)
const userId = 1

async function handleSend() {
  const msg = inputMessage.value.trim()
  if (!msg) return
  inputMessage.value = ''
  await chatStore.sendMsg(userId, msg)
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

watch(() => chatStore.messages.length, scrollToBottom)
</script>

<style scoped>
.chat-page {
  height: calc(100vh - 140px);
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.welcome {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}

.welcome h3 {
  margin: 16px 0 8px;
  color: #303133;
}

.suggestions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-item.USER {
  flex-direction: row-reverse;
}

.avatar {
  flex-shrink: 0;
}

.ai-avatar {
  background-color: #409eff;
  color: #fff;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
}

.message-bubble.USER {
  background-color: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-bubble.ASSISTANT {
  background-color: #f4f4f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message-text {
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  margin: 0;
}

.typing {
  animation: blink 1s infinite;
}

@keyframes blink {
  50% { opacity: 0.5; }
}

.input-area {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-area .el-input {
  flex: 1;
}
</style>
