package com.sky.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

//@AiService(chatMemory = "chatMemory") // 使用聊天记忆
@AiService(streamingChatModel = "qwenStreamingChatModel", chatMemoryProvider = "chatMemoryProvider", tools = "assistantTools", contentRetriever = "contentRetrieverWaiMai") // 隔离聊天记忆，参数需要加一个memoryId，此时不需要使用chatMemory参数
public interface Assistant {

    // @SystemMessage(fromResource = "template/SystemMessage.txt")
    // String chat(@UserMessage String message); // 仅使用chatMemory，并不能隔离聊天记忆

    // 使用memoryId隔离聊天记忆
    @SystemMessage(fromResource = "template/SystemMessage.txt")
    Flux<String> chat(@MemoryId Long memoryId, @UserMessage String message);
}
