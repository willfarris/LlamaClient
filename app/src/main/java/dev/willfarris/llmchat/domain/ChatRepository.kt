package dev.willfarris.llmchat.domain

interface ChatRepository {

    suspend fun getAllChats(): List<Chat>
    suspend fun saveChat(chat: Chat): Chat
    suspend fun deleteChat(chat: Chat)

    suspend fun getChatHistory(chat: Chat): List<Message>
    suspend fun saveMessage(chat: Chat, message: Message): Message
    suspend fun deleteMessage(chat: Chat, message: Message)

}