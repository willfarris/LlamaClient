package dev.willfarris.llmchat.data.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatHistoryDAO {

    @Query("SELECT * FROM messages")
    suspend fun getAllMessages(): List<ChatMessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId")
    suspend fun getMessagesInChat(chatId: Long): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chatMessageEntity: ChatMessageEntity): Long

    @Delete
    suspend fun deleteMessage(chatMessageEntity: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateConversation(newChat: ChatConversationEntity): Long

    @Query("SELECT * FROM chats")
    suspend fun getAllConversations(): List<ChatConversationEntity>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    suspend fun getConversationById(chatId: Long): ChatConversationEntity

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteConversationById(chatId: Long)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesWithChatId(chatId: Long)

}