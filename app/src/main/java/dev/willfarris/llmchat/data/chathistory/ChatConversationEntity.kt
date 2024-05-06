package dev.willfarris.llmchat.data.chathistory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatConversationEntity(
    val modelName: String,
    val title: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)