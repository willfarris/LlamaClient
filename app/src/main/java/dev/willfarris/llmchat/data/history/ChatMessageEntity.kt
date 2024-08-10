package dev.willfarris.llmchat.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: Long = 0,
    val role: String,
    val messageContent: String,
    val modelName: String,
    val timeStamp: String,
)