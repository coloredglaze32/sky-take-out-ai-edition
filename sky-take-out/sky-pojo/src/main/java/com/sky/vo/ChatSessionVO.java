package com.sky.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSessionVO {
    private Long id;
    private Long memoryId;
    private String sessionTitle;
    private String lastMessage;
    private Integer messageCount;
    private LocalDateTime updateTime;
}
