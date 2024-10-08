package dev.willfarris.llmchat.data.api.ollama

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dev.willfarris.llmchat.data.api.ollama.chat.ChatPartialResponse
import dev.willfarris.llmchat.data.api.ollama.chat.ChatRequest
import dev.willfarris.llmchat.domain.health.PsInfo
import dev.willfarris.llmchat.data.api.ollama.model.Tags
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

interface OllamaAPIService {
    @Streaming
    @POST("api/chat")
    fun chat(
        @Body
        chatRequest: ChatRequest
    ): Call<ResponseBody>

    @GET("api/tags")
    fun tags(): Call<Tags>

    @GET("/")
    fun health(): Call<String>

    @GET("api/ps")
    fun ps(): Call<PsInfo>

    companion object {
        private var ollamaAPIService: OllamaAPIService? = null

        fun getInstance() : OllamaAPIService {
            if(ollamaAPIService == null) {
                val client = OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(OllamaPreferencesManager.endpointUrl)
                    .client(client)
                    .addConverterFactory(
                        ScalarsConverterFactory
                            .create()
                    )
                    .addConverterFactory(
                        GsonConverterFactory
                            .create()
                    )
                    .build()
                ollamaAPIService = retrofit.create(OllamaAPIService::class.java)
            }
            return ollamaAPIService!!
        }
    }
}