import request from './request'

/**
 * AI 对话 API
 */

// 同步对话
export function sendMessage(userId, data) {
  return request.post('/chat/send', data, { params: { userId } })
}

// 流式对话 (SSE)
export function sendMessageStream(userId, data) {
  return fetch(`/api/chat/stream?userId=${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${localStorage.getItem('token') || ''}`,
    },
    body: JSON.stringify(data),
  })
}
