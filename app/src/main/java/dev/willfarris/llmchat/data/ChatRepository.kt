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
    suspend fun getAllMessages(chatId: Long): List<ChatMessage> = chatHistoryDAO.getMessagesInChat(chatId).map {m ->
        ChatMessage(
            m.role,
            m.messageContent,
            m.modelName,
        )
    }

    suspend fun getAllConversations(): List<ChatSummaryUiContent> = chatHistoryDAO.getAllConversations().map { c ->
        ChatSummaryUiContent(
            c.id,
            mutableStateOf(c.title),
        )
    }

    suspend fun createChat(title: String, modelName: String): ChatSummaryUiContent {
        val chatId = chatHistoryDAO.newConversation(ChatConversationEntity(title = title, modelName = modelName))
        return ChatSummaryUiContent(chatId, mutableStateOf(title))
    }

    suspend fun sendMessage(chatId: Long, modelName: String, message: String) =  flow {
        val newUserMessage = ChatMessageEntity(0, chatId, "user", message, modelName, "")
        chatHistoryDAO.insertMessage(newUserMessage)

        val chatRequest = ChatRequest(
            model = modelName,
            messages = getAllMessages(chatId),
            stream = true,
            keepAlive = "24h",
            system = null,
            options = ChatOptions(
                numCtx = OllamaPreferencesManager.contextSize
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
        chatHistoryDAO.updateConversation(fromUiContent.chatId, fromUiContent.chatName.value)
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