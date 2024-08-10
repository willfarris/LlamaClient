package dev.willfarris.llmchat.data.api.ollama.health

import dev.willfarris.llmchat.data.api.ollama.model.ModelInfo

data class PsInfo(
    val models: List<ModelInfo>,
)
