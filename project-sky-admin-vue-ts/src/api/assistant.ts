import request from '@/utils/request'

/**
 * 智能助手聊天相关接口
 */

// 发送聊天消息（流式响应）
export const sendChatMessage = (data: { memoryId: number; message: string }) => {
  return request({
    url: '/chat/assistant',
    method: 'post',
    data
  })
}

// 获取当前管理员的所有聊天会话列表
export const getChatSessions = () => {
  return request({
    url: '/chat/sessions',
    method: 'get'
  })
}

// 创建新的聊天会话
export const createNewSession = () => {
  return request({
    url: '/chat/sessions',
    method: 'post'
  })
}

// 获取指定会话的聊天历史记录
export const getChatHistory = (memoryId: number) => {
  return request({
    url: `/chat/history/${memoryId}`,
    method: 'get'
  })
}

// 删除聊天会话
export const deleteSession = (sessionId: number) => {
  return request({
    url: `/chat/sessions/${sessionId}`,
    method: 'delete'
  })
} 