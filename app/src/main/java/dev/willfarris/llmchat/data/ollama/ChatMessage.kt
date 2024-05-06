package dev.willfarris.llmchat.data.ollama

data class ChatMessage(
    val role: String,
    var content: String,
    val modelName: String,
)