package dev.willfarris.llmchat.data.chathistory

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager

@Entity(tableName = "chats")
data class ChatConversationEntity(
    val modelName: String,
    val title: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    val preferredModel: String? = null,
    // Context size
    val contextSize: Int = OllamaPreferencesManager.contextSize,
    // System prompt
    val systemPrompt: String = OllamaPreferencesManager.systemPrompt,
)