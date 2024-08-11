package dev.willfarris.llmchat.domain

data class Chat(
    val id: Long = 0,
    val model: String?,
    val title: String,
    val contextSize: Int,
    val systemPrompt: String,

    //val messages: List<Message>
)