package dev.willfarris.llmchat.data.ollama

import com.google.gson.annotations.SerializedName


data class ChatOptions(
    // Enable Mirostat sampling for controlling perplexity.
    // (default: 0, 0 = disabled, 1 = Mirostat, 2 = Mirostat 2.0)
    val mirostat: Int? = null,

    // Influences how quickly the algorithm responds to feedback from the generated text.
    // A lower learning rate will result in slower adjustments,
    // while a higher learning rate will make the algorithm more responsive.
    // (Default: 0.1)
    @SerializedName("mirostat_eta")
    val mirostatEta: Float? = null,

    // Controls the balance between coherence and diversity of the output.
    // A lower value will result in more focused and coherent text. (Default: 5.0)
    @SerializedName("mirostat_tau")
    val mirostatTau: Float? = null,

    // Sets the size of the context window used to generate the next token.
    // (Default: 2048)
    @SerializedName("num_ctx")
    val numCtx: Int? = null,

    // Sets how far back for the model to look back to prevent repetition.
    // (Default: 64, 0 = disabled, -1 = num_ctx)
    @SerializedName("repeat_last_n")
    val repeatLastN: Int? = null,

    // Sets how strongly to penalize repetitions.
    // A higher value (e.g., 1.5) will penalize repetitions more strongly,
    // while a lower value (e.g., 0.9) will be more lenient.
    // (Default: 1.1)
    @SerializedName("repeat_penalty")
    val repeatPenalty: Float? = null,// = 1.1f,

    // The temperature of the model.
    // Increasing the temperature will make the model answer more creatively.
    // (Default: 0.8)
    val temperature: Float? = 0.7f,

    // Sets the random number seed to use for generation.
    // Setting this to a specific number will make the model generate the same text for the same prompt.
    // (Default: 0)
    val seed: Int? = null,

    // Sets the stop sequences to use.
    // When this pattern is encountered the LLM will stop generating text and return.
    // Multiple stop patterns may be set by specifying multiple separate stop parameters in a modelfile.
    val stop: String? = null,

    // Tail free sampling is used to reduce the impact of less probable tokens from the output.
    // A higher value (e.g., 2.0) will reduce the impact more,
    // while a value of 1.0 disables this setting.
    // (default: 1)
    @SerializedName("tfs_z")
    val tfsZ: Float? = null,//1f,

    // Maximum number of tokens to predict when generating text.
    // (Default: 128, -1 = infinite generation, -2 = fill context)
    @SerializedName("num_predict")
    val numPredict: Int? = -1,

    // Reduces the probability of generating nonsense.
    // A higher value (e.g. 100) will give more diverse answers,
    // while a lower value (e.g. 10) will be more conservative.
    // (Default: 40)
    @SerializedName("top_k")
    val topK: Int? = null,

    // Works together with top-k. A higher value (e.g., 0.95) will lead to more diverse text,
    // while a lower value (e.g., 0.5) will generate more focused and conservative text.
    // (Default: 0.9) 	float 	top_p 0.9
    @SerializedName("top_p")
    val topP: Float? = null,
)