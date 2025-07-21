package com.sky.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatHistoryVO {
    private List<ChatMessageVO> messages;

    public ChatHistoryVO() {}

    public ChatHistoryVO(List<ChatMessageVO> messages) {
        this.messages = messages;
    }

    public List<ChatMessageVO> getMessages() { return messages; }
    public void setMessages(List<ChatMessageVO> messages) { this.messages = messages; }
}
