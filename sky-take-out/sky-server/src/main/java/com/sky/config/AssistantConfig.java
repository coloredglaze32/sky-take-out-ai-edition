package com.sky.config;

import com.sky.store.MongoChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Autowired
    private EmbeddingStore embeddingStore;
    @Autowired
    private EmbeddingModel embeddingModel;


    // 定义聊天记忆，存储10条记忆，并在Assistant中使用注解调用
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.withMaxMessages(10);
    }

    // 隔离聊天记忆
//    public ChatMemoryProvider chatMemoryProvider(){
//        return memoryId -> MessageWindowChatMemory.builder().id(memoryId).maxMessages(10).build();
//    }

    // 添加持久化聊天记忆
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(mongoChatMemoryStore)
                .build();
    }

    @Bean
    public ContentRetriever contentRetrieverWaiMai() {
    // 创建一个 EmbeddingStoreContentRetriever 对象，用于从嵌入存储中检索内容
            return EmbeddingStoreContentRetriever
                    .builder()
    // 设置用于生成嵌入向量的嵌入模型
                    .embeddingModel(embeddingModel)
    // 指定要使用的嵌入存储
                    .embeddingStore(embeddingStore)
    // 设置最大检索结果数量，这里表示最多返回 1 条匹配结果
                    .maxResults(1)
    // 设置最小得分阈值，只有得分大于等于 0.8 的结果才会被返回
                    .minScore(0.8)
    // 构建最终的 EmbeddingStoreContentRetriever 实例
                    .build();
    }

}
