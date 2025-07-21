package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.ChatSession;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatSessionMapper {
    /**
     * 新增聊天会话
     */
    @Insert("INSERT INTO chat_sessions (admin_id, memory_id, session_title, last_message, create_time, update_time, is_deleted) " +
            "VALUES (#{adminId}, #{memoryId}, #{sessionTitle}, #{lastMessage}, #{createTime}, #{updateTime}, #{isDeleted})")
    void insert(ChatSession chatSession);

    /**
     * 根据管理员ID查询会话列表
     */
    @Select("SELECT * FROM chat_sessions WHERE admin_id = #{adminId} AND is_deleted = 0 " +
            "ORDER BY update_time DESC")
    List<ChatSession> findByAdminId(Long adminId);

    /**
     * 根据ID查询会话
     */
    @Select("SELECT * FROM chat_sessions WHERE id = #{id} AND is_deleted = 0")
    ChatSession findById(Long id);

    /**
     * 更新会话信息
     */
    @Update("UPDATE chat_sessions SET session_title = #{sessionTitle}, " +
            "last_message = #{lastMessage}, " +
            "update_time = NOW() WHERE id = #{id}")
    void update(ChatSession chatSession);

    /**
     * 删除会话（软删除）
     */
    @Delete("DELETE FROM chat_sessions WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 根据memoryId查询会话
     */
    @Select("SELECT * FROM chat_sessions WHERE memory_id = #{memoryId} AND is_deleted = 0")
    ChatSession findByMemoryId(Long memoryId);
}
