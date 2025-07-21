package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    private Long id;
    private Long adminId;
    private Long memoryId;
    private String sessionTitle;
    private String lastMessage;
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
