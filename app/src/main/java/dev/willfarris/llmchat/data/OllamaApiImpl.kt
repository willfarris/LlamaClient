package dev.willfarris.llmchat.data

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dev.willfarris.llmchat.data.api.ollama.OllamaAPIService
import dev.willfarris.llmchat.data.api.ollama.chat.ChatMessage
import dev.willfarris.llmchat.data.api.ollama.chat.ChatPartialResponse
import dev.willfarris.llmchat.data.api.ollama.chat.ChatRequest
import dev.willfarris.llmchat.data.history.ChatMessageEntity
import dev.willfarris.llmchat.domain.Chat
import dev.willfarris.llmchat.domain.Message
import dev.willfarris.llmchat.domain.Model
import dev.willfarris.llmchat.domain.OllamaApi
import dev.willfarris.llmchat.domain.health.HealthStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

class OllamaApiImpl(
    private val ollamaAPIService: OllamaAPIService
): OllamaApi {
    override suspend fun getAvailableModels(): List<Model> {
        val tags = ollamaAPIService.tags().execute()
        return tags.body()!!.models.map {
            Model(
                name = it.name,
                size = it.size,
                sizeVram = it.sizeVram,
                expiresAt = it.expiresAt,
            )
        }
    }

    override suspend fun getLoadedModels(): List<Model> {
        val ps = ollamaAPIService.ps().execute()
        return ps.body()!!.models.map {
            Model(
                name = it.name,
                size = it.size,
                sizeVram = it.sizeVram,
                expiresAt = it.expiresAt,
            )
        }
    }

    override suspend fun sendMessage(chat: Chat, messageHistory: List<Message>)= flow {
        val messages = messageHistory.map {
            ChatMessage(
                role = it.role,
                content = it.content,
                modelName = chat.model!!,
            )
        }
        val chatRequest = ChatRequest(
            model = chat.model!!,
            messages = messages,
            stream = true,
        )
        val response = ollamaAPIService.chat(chatRequest).execute()
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
                    emit(Message(
                        content = partialResponse.message.content,
                        role = partialResponse.message.role,
                        modelName = partialResponse.model,
                    ))
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

    override suspend fun serverHealth(): HealthStatus {
        val heartbeat = try {
            ollamaAPIService.health().execute().body()!!
        } catch (e: IOException) {
            return HealthStatus("Error connecting to Ollama server", null, null)
        }
        val tags = try {
            ollamaAPIService.tags().execute().body()!!
        } catch(e: IOException) {
            return HealthStatus(heartbeat, null, null)
        }
        val psInfo = try {
            ollamaAPIService.ps().execute().body()!!
        } catch (e: IOException) {
            return HealthStatus(heartbeat, null, tags)
        }
        return HealthStatus(heartbeat, psInfo, tags)
    }

}