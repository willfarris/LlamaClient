package dev.willfarris.llmchat.ui.chatview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import dev.willfarris.llmchat.ChatAssistantApplication
import dev.willfarris.llmchat.data.api.ollama.model.ModelInfo
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.ConnectException
import java.util.UUID
import kotlin.time.Duration

data class ChatMessageUiContent(
    val role: String,
    val content: MutableState<String> = mutableStateOf(""),
    val modelName: String,
    val id: String = UUID.randomUUID().toString()
)

data class ChatSummaryUiContent(
    var chatId: Long,
    var chatTitle: MutableState<String>,
    var chatPrompt: MutableState<String>,
    var chatContextSize: MutableState<String>,
    var chatModel: String?,
)


fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

class ChatViewModel(application: ChatAssistantApplication): ViewModel() {
    private val repository = application.chatAssistantRepository

    var messagesList = mutableStateListOf<ChatMessageUiContent>()
        private set
    var chatsList = mutableStateListOf<ChatSummaryUiContent>()
        private set
    var modelsList = listOf<ModelInfo>()
        private set
    var activeModelsList = listOf<ModelInfo>()
        private set
    var curChatIndex: Int = OllamaPreferencesManager.lastChatIndex
        private set
    var curModelName: MutableState<String?> = mutableStateOf(null)
        private set

    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val appLoadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val heartbeatState = mutableStateOf(false)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runBlocking {
                    repository.getAllConversations().map { c ->
                        chatsList.add(c)
                    }
                    if (chatsList.isEmpty()) {
                        createNewChat()
                    } else {
                        selectChat(curChatIndex)
                    }
                }
                try {
                    modelsList = repository.getAvailableModels()
                } catch(e: Exception) {
                    errorMessage.update {
                        "Unable to fetch available models from Ollama server"
                    }
                }
                appLoadingState.update {
                    true
                }
            }
        }

        tickerFlow(Duration.parse("30s")).onEach { heartbeat() }.launchIn(viewModelScope)
    }

    private suspend fun heartbeat() {
        withContext(Dispatchers.IO) {
            try {
                val health = repository.serverHealth()
                heartbeatState.value = health.heartbeatPing == "Ollama is running"
                if(heartbeatState.value) {
                    activeModelsList = health.psInfo!!.models
                    modelsList = health.tags!!.models
                }
            } catch(e: IOException) {
                heartbeatState.value = false
            }

        }
    }

    fun triggerHeartbeat() {
        viewModelScope.launch {
            heartbeat()
        }
    }

    fun errorMessageComplete() {
        errorMessage.update {
            null
        }
    }

    fun sendMessageFromUser(text: String) {
        val c = chatsList[curChatIndex]
        val model = c.chatModel
        val id = c.chatId
        if(model == null) {
            errorMessage.update {
                "Select a model"
            }
            return
        }
        val newMessage = ChatMessageUiContent("user", mutableStateOf( text), model)
        messagesList.add(newMessage)

        var assistantResponse: ChatMessageUiContent? = null
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    repository.sendMessage(id, model, text).collect {
                        if(assistantResponse == null) {
                            assistantResponse = ChatMessageUiContent("assistant", mutableStateOf(""),model)
                            messagesList.add(assistantResponse!!)
                        }
                        assistantResponse!!.content.value = assistantResponse!!.content.value + it
                    }
                } catch (e: HttpException) {
                    val resp = e.response()!!
                    val str = "Error ${e.code()} ${e.message}, ${(resp.errorBody() as ResponseBody).string()}"
                    errorMessage.update {
                        str
                    }
                } catch (e: JsonSyntaxException) {
                    errorMessage.update {
                        e.message
                    }
                } catch (e: IndexOutOfBoundsException) {
                    errorMessage.update {
                        e.message
                    }
                } catch (e: SocketTimeoutException) {
                    errorMessage.update {
                        e.message
                    }
                } catch (e: ConnectException) {
                    errorMessage.update {
                        e.message
                    }
                }
            }
        }

    }

    fun deleteChat(chatIndexToDelete: Int) {
        viewModelScope.launch {
            val chatId = chatsList[chatIndexToDelete].chatId

            chatsList.removeAt(chatIndexToDelete)
            runBlocking {
                withContext(Dispatchers.IO) {
                    repository.deleteChatById(chatId)
                }
            }
            if(chatsList.isEmpty()) {
                createNewChat()
                return@launch
            }

            if(chatIndexToDelete == curChatIndex) selectChat(chatsList.lastIndex)
            else if(chatIndexToDelete < curChatIndex) selectChat(--curChatIndex)
        }
    }

    fun updateChatSettings(
        chatIndex: Int,
        chatTitle: String? = null,
        chatModel: String? = null,
        contextSize: String? = null,
        chatPrompt: String? = null,
    ) {
        val c = chatsList[chatIndex]
        if(chatTitle != null) c.chatTitle.value = chatTitle
        if(chatModel != null && chatModel != "") {
            c.chatModel = chatModel
            curModelName.value = chatModel
        }
        if(contextSize != null) c.chatContextSize.value = contextSize
        if(chatPrompt != null) c.chatPrompt.value = chatPrompt

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateConversation(
                    id = c.chatId,
                    title = c.chatTitle.value,
                    model = c.chatModel,
                    contextSize = c.chatContextSize.value.toIntOrNull()!!,
                    systemPrompt = c.chatPrompt.value
                )
            }
        }
    }

    fun selectChat(chatIndex: Int) {
        curChatIndex = chatIndex
        OllamaPreferencesManager.lastChatIndex = chatIndex
        messagesList.clear()
        val selectedChat = chatsList[curChatIndex]
        curModelName.value = selectedChat.chatModel
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.selectChat(selectedChat.chatId).map { m ->
                    withContext(Dispatchers.Main) {
                        messagesList.add(
                            ChatMessageUiContent(m.role, mutableStateOf(m.content), m.modelName)
                        )
                    }
                }
            }
        }
    }

    fun createNewChat() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newChat = repository.createChat("New Chat")
                withContext(Dispatchers.Main) {
                    chatsList.add(newChat)
                    val curModelTemp = curModelName.value
                    selectChat(chatsList.lastIndex)
                    if(curModelTemp != null) {
                        selectModel(curModelTemp)
                    }
                }
            }
        }
    }

    fun selectModel(modelIndex: Int) {
        updateChatSettings(curChatIndex, chatModel = modelsList[modelIndex].name)
        curModelName.value = chatsList[curChatIndex].chatModel
    }

    fun selectModel(modelName: String) {
        updateChatSettings(curChatIndex, chatModel = modelName)
        curModelName.value = chatsList[curChatIndex].chatModel
    }
}