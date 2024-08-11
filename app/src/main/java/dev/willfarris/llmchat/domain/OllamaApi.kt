package dev.willfarris.llmchat.domain

import dev.willfarris.llmchat.domain.health.HealthStatus
import kotlinx.coroutines.flow.Flow

interface OllamaApi {
    suspend fun getAvailableModels(): List<Model>
    suspend fun getLoadedModels(): List<Model>
    suspend fun sendMessage(chat: Chat, messageHistory: List<Message>): Flow<Message>
    suspend fun serverHealth(): HealthStatus
}