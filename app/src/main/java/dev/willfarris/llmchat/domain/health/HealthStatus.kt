package dev.willfarris.llmchat.domain.health

import dev.willfarris.llmchat.data.api.ollama.model.Tags

data class HealthStatus(
    val heartbeatPing: String,
    val psInfo: PsInfo?,
    val tags: Tags?
)
