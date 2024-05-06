package dev.willfarris.llmchat.data.chathistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatHistoryDAO {

    @Query("SELECT * from messages")
    suspend fun getAllMessages(): List<ChatMessageEntity>

    @Query("SELECT * from messages where chatId = :chatId")
    suspend fun getMessagesInChat(chatId: Long): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chatMessageEntity: ChatMessageEntity): Long

    @Delete
    suspend fun deleteMessage(chatMessageEntity: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newConversation(newChat: ChatConversationEntity): Long

    @Query("UPDATE chats SET title = :newTitle WHERE id = :chatId ")
    suspend fun updateConversation(chatId: Long, newTitle: String)

    @Query("SELECT * from chats")
    suspend fun getAllConversations(): List<ChatConversationEntity>

    @Query("DELETE from chats where id = :chatId")
    suspend fun deleteConversationById(chatId: Long)

    @Query("DELETE from messages where chatId = :chatId")
    suspend fun deleteMessagesWithChatId(chatId: Long)

}