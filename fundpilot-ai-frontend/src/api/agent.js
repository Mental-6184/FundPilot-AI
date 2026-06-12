import request from './request'

/**
 * AI Agent 对话 API
 */

// Agent 对话
export function agentChat(data) {
  return request.post('/agent/chat', data)
}

// 获取会话列表
export function getSessions() {
  return request.get('/agent/sessions')
}

// 删除会话
export function deleteSession(sessionId) {
  return request.delete(`/agent/sessions/${sessionId}`)
}
