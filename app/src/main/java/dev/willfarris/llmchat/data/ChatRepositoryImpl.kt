package dev.willfarris.llmchat.data

import android.util.Log
import dev.willfarris.llmchat.data.history.ChatConversationEntity
import dev.willfarris.llmchat.data.history.ChatHistoryDAO
import dev.willfarris.llmchat.data.history.ChatMessageEntity
import dev.willfarris.llmchat.domain.Chat
import dev.willfarris.llmchat.domain.ChatRepository
import dev.willfarris.llmchat.domain.Message

class ChatRepositoryImpl(
    private val chatHistoryDAO: ChatHistoryDAO
): ChatRepository {

    /*suspend fun selectChat(chatId: Long): List<ChatMessage> = chatHistoryDAO.getMessagesInChat(chatId).map { m ->
        ChatMessage(
            m.role,
            m.messageContent,
            m.modelName,
        )
    }*/

    override suspend fun getChatHistory(chat: Chat): List<Message> {
        val messages = chatHistoryDAO.getMessagesInChat(chat.id).map {
            Message(
                id = it.id,
                role = it.role,
                content = it.messageContent,
                modelName = it.modelName,
            )
        }
        return messages
    }

    override suspend fun getAllChats(): List<Chat> {
        val chats = chatHistoryDAO.getAllConversations().map {
            Chat(
                id = it.id,
                model = it.preferredModel,
                title = it.title,
                contextSize = it.contextSize,
                systemPrompt = it.systemPrompt,
            )
        }
        return chats
    }

    /*suspend fun getAllConversations(): List<ChatSummaryUiContent> = chatHistoryDAO.getAllConversations().map { c ->
        ChatSummaryUiContent(
            chatId = c.id,
            chatTitle = mutableStateOf(c.title),
            chatModel = c.preferredModel,
            chatContextSize = mutableStateOf("${c.contextSize}"),
            chatPrompt = mutableStateOf(c.systemPrompt)
        )
    }*/

    override suspend fun saveChat(
        chat: Chat
    ): Chat {
        val chatEntity = ChatConversationEntity(
            id = chat.id,
            title = chat.title,
            preferredModel = chat.model,
            contextSize = chat.contextSize,
            systemPrompt = chat.systemPrompt,
        )
        val chatId = chatHistoryDAO.createOrUpdateConversation(chatEntity)
        return Chat(
            id = chatId,
            model = chat.model,
            title = chat.title,
            contextSize = chat.contextSize,
            systemPrompt = chat.systemPrompt,
        )
    }

    override suspend fun deleteChat(chat: Chat) {
        chatHistoryDAO.deleteConversationById(chat.id)
    }

    override suspend fun saveMessage(chat: Chat, message: Message): Message {
        val messageEntity = ChatMessageEntity(
            id = message.id,
            chatId = chat.id,
            role = message.role,
            messageContent = message.content,
            modelName = chat.model!!,
            timeStamp = "",
        )
        val messageId = chatHistoryDAO.insertMessage(messageEntity)
        return Message(
            id = messageId,
            role = message.role,
            content = message.content,
            modelName = message.modelName,
        )
    }

    override suspend fun deleteMessage(chat: Chat, message: Message) {
        chatHistoryDAO.deleteMessage(message.id)
    }


    /*suspend fun createChat(title: String): ChatSummaryUiContent {
        val chatConversationEntity = ChatConversationEntity(
            id = 0,
            title = title,
            preferredModel = null,
            contextSize = OllamaPreferencesManager.contextSize,
            systemPrompt = OllamaPreferencesManager.systemPrompt,
        )
        val chatId = chatHistoryDAO.createOrUpdateConversation(chatConversationEntity)
        val chat = chatHistoryDAO.getConversationById(chatId)
        return ChatSummaryUiContent(
            chatId = chatId,
            chatTitle = mutableStateOf(chat.title),
            chatPrompt = mutableStateOf(chat.systemPrompt),
            chatContextSize = mutableStateOf("${chat.contextSize}"),
            chatModel = chat.preferredModel
        )
    }*/

    /*suspend fun sendMessage(chatId: Long, modelName: String, message: String) =  flow {
        val newUserMessage = ChatMessageEntity(0, chatId, "user", message, modelName, "")
        chatHistoryDAO.insertMessage(newUserMessage)

        val conversation = chatHistoryDAO.getConversationById(chatId)

        var messages = selectChat(chatId)
        val systemMessage = ChatMessage(
            "system",
            conversation.systemPrompt,
            modelName,
        )
        messages = listOf(systemMessage) + messages

        val chatRequest = ChatRequest(
            model = modelName,
            messages = messages,
            stream = true,
            keepAlive = "24h",
            options = ChatOptions(
                numCtx = conversation.contextSize,
            ),
        )
        var response = ""
        OllamaAPIService.streamChat(chatRequest).collect { partialResponse ->
            response += partialResponse.message.content
            emit(partialResponse.message.content)
        }
        chatHistoryDAO.insertMessage(ChatMessageEntity(0L, chatId, "assistant", response, modelName, "TODO"))
    }*/

    /*suspend fun updateConversation(
        id: Long,
        title: String,
        model: String?,
        contextSize: Int,
        systemPrompt: String,
    ) {
        val chatConversationEntity = ChatConversationEntity(
            id = id,
            title = title,
            preferredModel = model,
            contextSize = contextSize,
            systemPrompt = systemPrompt,
        )
        chatHistoryDAO.createOrUpdateConversation(chatConversationEntity)
    }*/

    /*fun getAvailableModels(): List<ModelInfo> {
        val tags = ollamaAPIService.tags().execute()
        return tags.body()!!.models
    }*/

    /*fun getLoadedModels(): List<ModelInfo> {
        val ps = ollamaAPIService.ps().execute()
        return ps.body()!!.models
    }*/

    /*suspend fun deleteChatById(chatId: Long) {
        chatHistoryDAO.deleteMessagesWithChatId(chatId)
        chatHistoryDAO.deleteConversationById(chatId)
    }*/

    /*fun serverHealth(): HealthStatus {
        val heartbeat = try {
            ollamaAPIService.health().execute().body()!!
        } catch (e: IOException) {
            return HealthStatus("Error connecting to Ollama server", null, null)
        }
        val tags = try {
            ollamaAPIService.tags().execute().body()!!
        } catch(e: IOException) {
            return HealthStatus(heartbeat, null, null)
        }
        val psInfo = try {
            ollamaAPIService.ps().execute().body()!!
        } catch (e: IOException) {
            return HealthStatus(heartbeat, null, tags)
        }
        return HealthStatus(heartbeat, psInfo, tags)
    }*/
}