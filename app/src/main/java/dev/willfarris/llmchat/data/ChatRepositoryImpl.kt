package dev.willfarris.llmchat.data

import dev.willfarris.llmchat.data.history.ChatConversationEntity
import dev.willfarris.llmchat.data.history.ChatHistoryDAO
import dev.willfarris.llmchat.data.history.ChatMessageEntity
import dev.willfarris.llmchat.domain.Chat
import dev.willfarris.llmchat.domain.ChatRepository
import dev.willfarris.llmchat.domain.Message

class ChatRepositoryImpl(
    private val chatHistoryDAO: ChatHistoryDAO
): ChatRepository {

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

}