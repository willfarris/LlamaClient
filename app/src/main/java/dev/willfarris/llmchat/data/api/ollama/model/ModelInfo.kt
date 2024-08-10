package dev.willfarris.llmchat.data.api.ollama.model

import com.google.gson.annotations.SerializedName

data class ModelInfo(
    val name: String,
    @SerializedName("modified_at")
    val modifiedAt: String,
    val size: Long,
    val digest: String,
    val details: Details,
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("size_vram")
    val sizeVram: Long?,
) {
    data class Details(
        val format: String,
        val family: String,
        val families: Any?,
        @SerializedName("parameter_size")
        val parameterSize: String,
        @SerializedName("quantization_level")
        val quantizationLevel: String
    )
}