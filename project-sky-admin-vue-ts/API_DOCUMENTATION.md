# 智能助手聊天 API 文档

## 前端实现说明

前端已经完成了以下功能：

- ✅ 聊天界面设计
- ✅ 会话管理（创建、切换、删除）
- ✅ 消息发送和接收
- ✅ 加载状态显示
- ✅ 错误处理
- ✅ API 接口调用

## 后端需要提供的 API 接口

### 1. 发送消息接口 ⭐ **核心接口**

**接口地址：** `POST /chat/send`

**请求头：**

```
Content-Type: application/json
token: {用户登录token}  // 如果需要用户认证
```

**请求参数：**

```json
{
  "sessionId": 1,                    // 会话ID (必填)
  "message": "用户输入的消息内容",       // 用户消息 (必填)
  "timestamp": 1703123456789         // 消息时间戳 (可选)
}
```

**响应格式：**

```json
{
  "code": 1,                        // 状态码：1=成功，0=失败
  "msg": "success",                 // 响应消息
  "data": {
    "sessionId": 1,                 // 会话ID
    "botReply": "AI助手的回复内容",    // AI回复 (必填)
    "timestamp": 1703123456790,     // 回复时间戳 (可选)
    "messageId": "msg_12345"        // 消息ID (可选)
  }
}
```

**错误响应：**

```json
{
  "code": 0,
  "msg": "错误信息描述",
  "data": null
}
```

---

### 2. 获取会话历史接口 (可选)

**接口地址：** `GET /chat/history/{sessionId}`

**请求参数：**

- `sessionId`: 会话ID (路径参数)

**响应格式：**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "sessionId": 1,
    "messages": [
      {
        "role": "user",              // 消息角色：user | assistant
        "content": "用户消息内容",
        "timestamp": 1703123456789
      },
      {
        "role": "assistant",
        "content": "AI回复内容", 
        "timestamp": 1703123456790
      }
    ]
  }
}
```

---

### 3. 创建会话接口 (可选)

**接口地址：** `POST /chat/session`

**请求参数：**

```json
{
  "title": "新会话1",              // 会话标题 (可选)
  "userId": 123                   // 用户ID (如果需要)
}
```

**响应格式：**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "sessionId": 10,
    "title": "新会话1"
  }
}
```

---

### 4. 删除会话接口 (可选)

**接口地址：** `DELETE /chat/session/{sessionId}`

**请求参数：**

- `sessionId`: 会话ID (路径参数)

**响应格式：**

```json
{
  "code": 1,
  "msg": "删除成功",
  "data": null
}
```

---

## 前端API调用流程

### 1. 发送消息流程

```
用户输入 -> 显示用户消息 -> 显示"正在思考中..." -> 调用API -> 显示AI回复
```

### 2. 错误处理

- 网络错误：显示"网络错误，请检查连接"
- 服务器错误：显示"服务器内部错误，请稍后重试"
- 401错误：显示"未授权访问"
- 404错误：显示"API接口不存在"

### 3. 加载状态

- 发送按钮显示loading状态
- 输入框禁用
- 消息列表显示"正在思考中..."

---

## 大模型集成建议

### 后端实现思路：

1. **接收前端请求**

   ```java
   @PostMapping("/chat/send")
   public Result sendMessage(@RequestBody ChatRequest request) {
       // 1. 验证参数
       // 2. 调用大模型API
       // 3. 返回结果
   }
   ```
2. **调用大模型**

   - 可以使用 OpenAI API、文心一言、通义千问等
   - 需要处理流式响应（可选）
   - 需要处理token限制和成本控制
3. **数据存储**（可选）

   ```sql
   -- 会话表
   CREATE TABLE chat_sessions (
       id BIGINT PRIMARY KEY,
       user_id BIGINT,
       title VARCHAR(255),
       created_at DATETIME
   );

   -- 消息表  
   CREATE TABLE chat_messages (
       id BIGINT PRIMARY KEY,
       session_id BIGINT,
       role VARCHAR(20), -- 'user' or 'assistant'
       content TEXT,
       created_at DATETIME
   );
   ```

### 大模型API调用示例：

```java
// 使用 OpenAI API
public String callOpenAI(String message) {
    // 构建请求
    Map<String, Object> request = new HashMap<>();
    request.put("model", "gpt-3.5-turbo");
    request.put("messages", Arrays.asList(
        Map.of("role", "user", "content", message)
    ));
  
    // 调用API
    String response = restTemplate.postForObject(
        "https://api.openai.com/v1/chat/completions",
        request,
        String.class
    );
  
    // 解析响应
    return parseResponse(response);
}
```

---

## 前端配置说明

### 1. API基础路径

在 `.env` 文件中配置：

```
VUE_APP_BASE_API=http://localhost:8080/api
```

### 2. 请求超时

当前配置为600秒，可根据大模型响应时间调整

### 3. 错误提示

使用Element UI的Message组件显示错误信息

---

## 测试建议

### 1. 模拟后端响应

可以先创建一个简单的模拟接口用于测试前端功能：

```javascript
// 临时测试接口
app.post('/api/chat/send', (req, res) => {
  setTimeout(() => {
    res.json({
      code: 1,
      msg: 'success',
      data: {
        sessionId: req.body.sessionId,
        botReply: `我收到了你的消息："${req.body.message}"，这是一个测试回复。`,
        timestamp: Date.now()
      }
    })
  }, 2000) // 模拟2秒延迟
})
```

### 2. 前端功能验证

- ✅ 创建新会话
- ✅ 发送消息
- ✅ 显示加载状态
- ✅ 错误处理
- ✅ 会话切换
- ✅ 删除会话

---

## 注意事项

1. **CORS配置**：确保后端允许前端域名访问
2. **认证授权**：根据项目需要添加用户认证
3. **速率限制**：防止API被滥用
4. **内容过滤**：对用户输入和AI输出进行适当过滤
5. **成本控制**：监控大模型API调用成本
6. **日志记录**：记录用户对话用于分析和改进

---

前端已经完全准备就绪，只需要后端提供 `/chat/send` 接口即可正常工作！
