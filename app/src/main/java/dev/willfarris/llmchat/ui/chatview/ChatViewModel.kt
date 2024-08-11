package dev.willfarris.llmchat.ui.chatview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.willfarris.llmchat.ChatAssistantApplication
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.domain.Chat
import dev.willfarris.llmchat.domain.Message
import dev.willfarris.llmchat.domain.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.time.Duration

/*data class ChatMessageUiContent(
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
)*/


fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

class ChatViewModel(application: ChatAssistantApplication): ViewModel() {
    private val repository = application.chatRepository
    private val ollamaApi = application.ollamaApi

    private val _messageList: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messagesList: SnapshotStateList<List<Message>> = _messageList

    private val _chatList: MutableStateFlow<List<Chat>> = MutableStateFlow(emptyList())
    val chatList: StateFlow<List<Chat>> = _chatList

    private val _modelsList: MutableStateFlow<List<Model>> = MutableStateFlow(emptyList())
    val modelsList: StateFlow<List<Model>> = _modelsList

    private val _activeModelsList: MutableStateFlow<List<Model>> = MutableStateFlow(emptyList())
    val activeModelsList: StateFlow<List<Model>> = _activeModelsList

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
                    _chatList.value = repository.getAllChats()
                    if (_chatList.value.isEmpty()) {
                        createNewChat()
                    } else {
                        selectChat(_chatList.value[curChatIndex].id)
                    }
                }
                try {
                    _modelsList.value = ollamaApi.getAvailableModels()
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
                val health = ollamaApi.serverHealth()
                heartbeatState.value = health.heartbeatPing == "Ollama is running"
                if(heartbeatState.value) {
                    _activeModelsList.value = health.psInfo!!.models.map {
                        Model(
                            name = it.name,
                            size = it.size,
                            sizeVram = it.sizeVram,
                            expiresAt = it.expiresAt,
                        )
                    }
                    _modelsList.value = health.tags!!.models.map {
                        Model(
                            name = it.name,
                            size = it.size,
                            sizeVram = it.sizeVram,
                            expiresAt = it.expiresAt,
                        )
                    }
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
        /*val c = chatsList[curChatIndex]
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
                    *//*repository.sendMessage(id, model, text).collect {
                        if(assistantResponse == null) {
                            assistantResponse = ChatMessageUiContent("assistant", mutableStateOf(""),model)
                            messagesList.add(assistantResponse!!)
                        }
                        assistantResponse!!.content.value = assistantResponse!!.content.value + it
                    }*//*
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
        }*/
    }

    fun deleteChat(chatIndexToDelete: Int) {
        viewModelScope.launch {
            val chat = _chatList.value[chatIndexToDelete]
            repository.deleteChat(chat)
            _chatList.value = repository.getAllChats()
            selectChat(_chatList.value[0].id)

            /*val chatId = chatsList[chatIndexToDelete].chatId
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
            else if(chatIndexToDelete < curChatIndex) selectChat(--curChatIndex)*/
        }
    }

    /*private fun findChatById(chatId: Long): Chat {
        _chatList.value.forEach {
            if(it.id == chatId) {
                return it
            }
        }
        throw IndexOutOfBoundsException()
    }*/

    fun updateChatSettings(
        chat: Chat,
        chatTitle: String? = null,
        chatModel: String? = null,
        contextSize: String? = null,
        chatPrompt: String? = null,
    ) {
        viewModelScope.launch {
            val contextSizeInt = contextSize?.toIntOrNull()
            val updatedChat = Chat(
                id = chat.id,
                title = chatTitle ?: chat.title,
                model = chatModel ?: chat.model,
                contextSize = contextSizeInt ?: chat.contextSize,
                systemPrompt = chatPrompt ?: chat.systemPrompt,
            )
            repository.saveChat(updatedChat)
            _chatList.value = repository.getAllChats()
            selectChat(chat.id)
        }
        /*if(chatTitle != null) c.title = chatTitle
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
        }*/
    }

    fun selectChat(chatId: Long) {
        viewModelScope.launch {
            chatList.value.forEachIndexed { i, c ->
                if (c.id == chatId) {
                    curChatIndex = i
                    OllamaPreferencesManager.lastChatIndex = curChatIndex
                    val messages = repository.getChatHistory(c)
                    _messageList.value = messages
                }
            }
        }
        /*messagesList.clear()
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
        }*/
    }

    fun createNewChat() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newChat = Chat(
                    title = "New Chat",
                    contextSize = OllamaPreferencesManager.contextSize,
                    systemPrompt = OllamaPreferencesManager.systemPrompt,
                    model = curModelName.value
                )
                val chatId = repository.saveChat(newChat).id
                _chatList.value = repository.getAllChats()
                selectChat(chatId)
                /*withContext(Dispatchers.Main) {
                    chatsList.add(newChat)
                    val curModelTemp = curModelName.value
                    selectChat(chatsList.lastIndex)
                    if(curModelTemp != null) {
                        selectModel(curModelTemp)
                    }
                }*/
            }
        }
    }

    fun selectModel(modelIndex: Int) {
        val chat = chatList.value[curChatIndex]
        updateChatSettings(chat, chatModel = modelsList.value[modelIndex].name)
        curModelName.value = chat.model
    }

    /*fun selectModel(modelName: String) {
        updateChatSettings(curChatIndex, chatModel = modelName)
        curModelName.value = chatList.value[curChatIndex].model
    }*/
}