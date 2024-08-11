package dev.willfarris.llmchat.domain

data class Model(
    val name: String,
    val size: Long,
    val sizeVram: Long?,
    val expiresAt: String?,
)