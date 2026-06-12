import { defineStore } from 'pinia'
import { ref } from 'vue'
import { agentChat, getSessions, deleteSession } from '@/api/agent'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const sessionId = ref('')
  const sessions = ref([])
  const loading = ref(false)

  // 发送消息
  async function sendMessage(content) {
    // 添加用户消息
    messages.value.push({ role: 'USER', content, timestamp: Date.now() })
    loading.value = true

    try {
      const res = await agentChat({
        sessionId: sessionId.value,
        message: content,
      })
      sessionId.value = res.data.sessionId
      messages.value.push({
        role: 'ASSISTANT',
        content: res.data.content,
        timestamp: Date.now(),
      })
    } catch (error) {
      messages.value.push({
        role: 'ASSISTANT',
        content: '抱歉，处理您的请求时遇到了问题，请稍后重试。',
        timestamp: Date.now(),
      })
    } finally {
      loading.value = false
    }
  }

  // 加载会话列表
  async function fetchSessions() {
    try {
      const res = await getSessions()
      sessions.value = res.data || []
    } catch {
      sessions.value = []
    }
  }

  // 切换会话
  function switchSession(newSessionId) {
    sessionId.value = newSessionId
    messages.value = []
  }

  // 创建新会话
  function newSession() {
    sessionId.value = ''
    messages.value = []
  }

  // 删除会话
  async function removeSession(sid) {
    await deleteSession(sid)
    sessions.value = sessions.value.filter((s) => s !== sid)
    if (sessionId.value === sid) {
      newSession()
    }
  }

  // 清空消息
  function clearMessages() {
    messages.value = []
    sessionId.value = ''
  }

  return {
    messages,
    sessionId,
    sessions,
    loading,
    sendMessage,
    fetchSessions,
    switchSession,
    newSession,
    removeSession,
    clearMessages,
  }
})
