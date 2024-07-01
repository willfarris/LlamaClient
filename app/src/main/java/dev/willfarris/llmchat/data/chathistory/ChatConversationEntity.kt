package dev.willfarris.llmchat.data.chathistory

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager

@Entity(tableName = "chats")
data class ChatConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val preferredModel: String,
    val contextSize: Int,
    val systemPrompt: String,
)