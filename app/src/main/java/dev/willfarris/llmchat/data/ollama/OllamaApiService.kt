package dev.willfarris.llmchat.data.ollama

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
                        GsonConverterFactory
                            .create()
                    )
                    .build()
                ollamaAPIService = retrofit.create(OllamaAPIService::class.java)
            }
            return ollamaAPIService!!
        }

        fun streamChat(chatRequest: ChatRequest) = flow {
            coroutineScope {
                val response = getInstance().chat(chatRequest).execute()
                val gson = Gson()
                if (response.isSuccessful) {
                    val input = response.body()?.byteStream()?.bufferedReader() ?: throw Exception()
                    var doneResponse = false
                    while (!doneResponse) {
                        val line =
                            withContext(Dispatchers.IO) {
                                input.readLine()
                            } ?: continue
                        try {
                            val partialResponse =
                                gson.fromJson(line, ChatPartialResponse::class.java)
                            doneResponse = partialResponse.done
                            emit(partialResponse)
                        } catch (e: JsonSyntaxException) {
                            throw e
                        }
                    }
                    withContext(Dispatchers.IO) {
                        input.close()
                    }
                } else {
                    throw HttpException(response)
                }
            }
        }

    }
}