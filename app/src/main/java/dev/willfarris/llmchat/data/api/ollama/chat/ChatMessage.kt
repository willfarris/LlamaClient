package dev.willfarris.llmchat.data.api.ollama.chat

data class ChatMessage(
    val role: String,
    var content: String,
    val modelName: String,
)