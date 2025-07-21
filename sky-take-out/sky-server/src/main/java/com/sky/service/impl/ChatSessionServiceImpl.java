package com.sky.service.impl;

import com.sky.bean.ChatMessages;
import com.sky.context.BaseContext;
import com.sky.dto.ChatMessageDTO;
import com.sky.entity.ChatSession;
import com.sky.mapper.ChatSessionMapper;
import com.sky.service.ChatSessionService;
import com.sky.vo.ChatHistoryVO;
import com.sky.vo.ChatMessageVO;
import com.sky.vo.ChatSessionCreateVO;
import com.sky.vo.ChatSessionVO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.UserMessage;
import org.apache.james.mime4j.dom.datetime.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(ChatSessionServiceImpl.class);


    @Override
    public List<ChatSessionVO> getChatSessions(Long adminId) {
        log.info("获取管理员{}的会话列表", adminId);

        List<ChatSession> sessions = chatSessionMapper.findByAdminId(adminId);

        return sessions.stream()
                .map(this::convertSessionVO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatSessionCreateVO createNewSession() {
        Long adminId = BaseContext.getCurrentId();
        log.info("创建新的会话，管理员ID：{}", adminId);
        Long memoryId = generateMemoryId();
        ChatSession chatSession = ChatSession.builder()
                .adminId(adminId)
                .memoryId(memoryId)
                .sessionTitle("新对话")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .lastMessage("你好，我是苍穹小智，有什么可以帮你？")
                .isDeleted(0)
                .build();
        chatSessionMapper.insert(chatSession);

        // 返回结果
        ChatSessionCreateVO result = new ChatSessionCreateVO();
        result.setSessionId(chatSession.getId());
        result.setMemoryId(memoryId);

        log.info("创建会话成功，sessionId: {}, memoryId: {}", chatSession.getId(), memoryId);
        return result;
    }

    @Override
    public void updateSessionTitle(Long memoryId, String lastMessage) {
        // 获取当前memoryId对应的ChatSession对象，并将最后一条聊天消息更新
        ChatSession session = chatSessionMapper.findByMemoryId(memoryId);
        session.setLastMessage(lastMessage);
        // 更新
        chatSessionMapper.update(session);
    }

    @Override
    public Flux<String> sendMessage(ChatMessageDTO chatMessageDTO) {
        return null;
    }

    @Override
    public ChatHistoryVO getChatHistory(Long memooryId) {
        Criteria criteria = Criteria.where("memoryId").is(memooryId);
        Query query = new Query(criteria);
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);
        if(chatMessages == null){
            return new ChatHistoryVO();
        }
        String contentJson = chatMessages.getContent();
        List<ChatMessage> messages = ChatMessageDeserializer.messagesFromJson(contentJson);
        List<ChatMessageVO> voList = messages.stream()
                .map(this::convertMessageToVO)
                .collect(Collectors.toList());

        return new ChatHistoryVO(voList);
    }

    // 会话转VO
    private ChatSessionVO convertSessionVO(ChatSession session) {
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        vo.setMemoryId(session.getMemoryId());
        vo.setSessionTitle(session.getSessionTitle());
        vo.setLastMessage(session.getLastMessage());
        vo.setUpdateTime(session.getUpdateTime());
        return vo;
    }

    // 消息转VO（新方法）
    private ChatMessageVO convertMessageToVO(ChatMessage message) {
        if (message instanceof AiMessage) {
            AiMessage aiMsg = (AiMessage) message;
            // 当调用Tools工具函数时，text=null
            // TODO: 调用工具函数，没有text属性
            String text = (aiMsg.text() == null || aiMsg.text().isEmpty())? "": aiMsg.text();
            return new ChatMessageVO("assistant", text);
        } else if (message instanceof UserMessage) {
            UserMessage userMsg = (UserMessage) message;
            String content = userMsg.contents().isEmpty() ? "" : userMsg.contents().get(0).toString();
            return new ChatMessageVO("user", content);
        }
        return new ChatMessageVO("unknown", "");
    }

    @Override
    public void deleteSession(Long sessionId) {
        Long adminId = BaseContext.getCurrentId();
        log.info("管理员{}删除会话，sessionId: {}", adminId, sessionId);

        // 验证权限
        ChatSession session = chatSessionMapper.findById(sessionId);
        if (session == null || !session.getAdminId().equals(adminId)) {
            throw new RuntimeException("会话不存在或无权访问");
        }

        // 软删除
        chatSessionMapper.deleteById(sessionId);
        log.info("删除会话成功，sessionId: {}", sessionId);
    }

    private Long generateMemoryId() {
        Random random = new Random();
        // 生成一个较大的随机数作为memoryId
        return 10000000L + random.nextInt(90000000);
    }
}
