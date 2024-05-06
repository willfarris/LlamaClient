package dev.willfarris.llmchat.data.ollama

import com.google.gson.annotations.SerializedName

data class ChatPartialResponse(
    val model: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    val message: ChatMessage,
    val done: Boolean,
)