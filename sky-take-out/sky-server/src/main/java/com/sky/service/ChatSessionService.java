package com.sky.service;

import com.sky.dto.ChatMessageDTO;
import com.sky.vo.ChatHistoryVO;
import com.sky.vo.ChatSessionCreateVO;
import com.sky.vo.ChatSessionVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatSessionService {
    List<ChatSessionVO> getChatSessions(Long adminId);

    /**
     * 创建新会话
     */
    ChatSessionCreateVO createNewSession();

    /**
     * 发送消息（流式响应）
     */
    Flux<String> sendMessage(ChatMessageDTO chatMessageDTO);

    /**
     * 获取聊天历史
     */
    ChatHistoryVO getChatHistory(Long memoryId);

    /**
     * 删除会话
     */
    void deleteSession(Long sessionId);

    void updateSessionTitle(Long memoryId, String lastMessage);
}
