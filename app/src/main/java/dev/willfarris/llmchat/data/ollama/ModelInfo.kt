package dev.willfarris.llmchat.data.ollama

import com.google.gson.annotations.SerializedName

data class ModelInfo(
    val name: String,
    @SerializedName("modified_at")
    val modifiedAt: String,
    val size: Long,
    val digest: String,
    val details: Details
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