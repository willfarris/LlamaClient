package dev.willfarris.llmchat.domain

data class Message(
    val id: Long = 0,
    val role: String,
    val content: String,
    val modelName: String,
)