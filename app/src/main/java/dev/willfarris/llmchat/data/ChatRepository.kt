package dev.willfarris.llmchat.data

import androidx.compose.runtime.mutableStateOf
import dev.willfarris.llmchat.data.chathistory.ChatConversationEntity
import dev.willfarris.llmchat.data.chathistory.ChatHistoryDAO
import dev.willfarris.llmchat.data.chathistory.ChatMessageEntity
import dev.willfarris.llmchat.data.ollama.ChatMessage
import dev.willfarris.llmchat.data.ollama.ChatOptions
import dev.willfarris.llmchat.data.ollama.ChatRequest
import dev.willfarris.llmchat.data.ollama.ModelInfo
import dev.willfarris.llmchat.data.ollama.OllamaAPIService
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.ui.chatview.ChatSummaryUiContent
import kotlinx.coroutines.flow.flow

class ChatRepository(
    private val chatHistoryDAO: ChatHistoryDAO,
    private val ollamaAPIService: OllamaAPIService
) {
    suspend fun selectChat(chatId: Long): List<ChatMessage> = chatHistoryDAO.getMessagesInChat(chatId).map { m ->
        ChatMessage(
            m.role,
            m.messageContent,
            m.modelName,
        )
    }

    suspend fun getAllConversations(): List<ChatSummaryUiContent> = chatHistoryDAO.getAllConversations().map { c ->
        ChatSummaryUiContent(
            chatId = c.id,
            chatTitle = mutableStateOf(c.title),
            chatModel =  mutableStateOf(c.preferredModel),
            chatContextSize = mutableStateOf("${c.contextSize}"),
            chatPrompt = mutableStateOf(c.systemPrompt)
        )
    }

    suspend fun createChat(title: String): ChatSummaryUiContent {
        val chatConversationEntity = ChatConversationEntity(
            id = 0,
            title = title,
            preferredModel = "",
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
            chatModel = mutableStateOf(chat.preferredModel)
        )
    }

    suspend fun sendMessage(chatId: Long, modelName: String, message: String) =  flow {
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
    }

    suspend fun updateChat(fromUiContent: ChatSummaryUiContent) {
        val chatConversationEntity = ChatConversationEntity(
            id = fromUiContent.chatId,
            title = fromUiContent.chatTitle.value,
            preferredModel = fromUiContent.chatModel.value,
            contextSize = fromUiContent.chatContextSize.value.toInt(),
            systemPrompt = fromUiContent.chatPrompt.value,
        )
        chatHistoryDAO.createOrUpdateConversation(chatConversationEntity)
    }

    fun getAvailableModels(): List<ModelInfo> {
        val tags = ollamaAPIService.tags().execute()
        return tags.body()!!.models
    }

    suspend fun deleteChatById(chatId: Long) {
        chatHistoryDAO.deleteMessagesWithChatId(chatId)
        chatHistoryDAO.deleteConversationById(chatId)
    }
}