package dev.willfarris.llmchat.data.api.ollama.chat

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean,
    @SerializedName("keep_alive")
    val keepAlive: String = "24h",
    val system: String? = null,
    val options: ChatOptions? = null,
)