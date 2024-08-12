package dev.willfarris.llmchat.ui.chatview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import dev.willfarris.llmchat.ChatAssistantApplication
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.domain.Chat
import dev.willfarris.llmchat.domain.Message
import dev.willfarris.llmchat.domain.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.time.Duration

/*data class ChatMessageUiContent(
    val role: String,
    val content: MutableState<String> = mutableStateOf(""),
    val modelName: String,
    val id: String = UUID.randomUUID().toString()
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

    var messageList = mutableStateListOf<Message>()
        private set
    var chatList = mutableStateListOf<Chat>()
        private set
    var modelsList = mutableListOf<Model>()
        private set
    var activeModelsList = mutableStateListOf<Model>()
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
                    chatList.clear()
                    chatList.addAll(repository.getAllChats())
                    if (chatList.isEmpty()) {
                        createNewChat()
                    } else {
                        selectChat(chatList[curChatIndex].id)
                    }
                }
                try {
                    modelsList = ollamaApi.getAvailableModels().toMutableList()
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
                    activeModelsList.clear()
                    activeModelsList.addAll(health.psInfo!!.models.map {
                        Model(
                            name = it.name,
                            size = it.size,
                            sizeVram = it.sizeVram,
                            expiresAt = it.expiresAt,
                        )
                    })
                    modelsList.clear()
                    modelsList.addAll(health.tags!!.models.map {
                        Model(
                            name = it.name,
                            size = it.size,
                            sizeVram = it.sizeVram,
                            expiresAt = it.expiresAt,
                        )
                    })
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val chat = chatList[curChatIndex]
                val model = chat.model
                if (model == null) {
                    errorMessage.update {
                        "Select a model"
                    }
                    return@withContext
                }

                val userMessage = Message(
                    content = text,
                    role = "user",
                    modelName = model
                )
                repository.saveMessage(chat, userMessage)
                messageList.add(userMessage)

                val systemPrompt = Message(
                    role = "assistant",
                    content = chat.systemPrompt,
                    modelName = model
                )

                val messages = listOf(systemPrompt) + repository.getChatHistory(chat)

                var response: Message? = null
                try {
                    ollamaApi.sendMessage(chat, messages).collect { m ->
                        if (response == null) {
                            response = Message(
                                content = m.content,
                                role = m.role,
                                modelName = m.modelName,
                            )
                        } else {
                            response = Message(
                                id = response!!.id,
                                role = response!!.role,
                                content = response!!.content + m.content,
                                modelName = response!!.modelName
                            )
                        }
                        response = repository.saveMessage(chat, response!!)
                        messageList.clear()
                        messageList.addAll(repository.getChatHistory(chat))
                    }
                } catch (e: HttpException) {
                    val resp = e.response()!!
                    val str = "Error ${e.code()} ${e.message}, ${(resp.errorBody())?.string()}"
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
            val chat = chatList[chatIndexToDelete]
            repository.deleteChat(chat)
            chatList.clear()
            chatList.addAll(repository.getAllChats())
            if(chatList.isEmpty()) {
                createNewChat()
                return@launch
            }
            if(chatIndexToDelete == curChatIndex) selectChat(chatList.last().id)
            else if(chatIndexToDelete < curChatIndex) selectChat(chatList[--curChatIndex].id)

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
            chatList.clear()
            chatList.addAll(repository.getAllChats())
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
            chatList.forEachIndexed { i, c ->
                if (c.id == chatId) {
                    curChatIndex = i
                    OllamaPreferencesManager.lastChatIndex = curChatIndex
                    val messages = repository.getChatHistory(c)
                    messageList.clear()
                    messageList.addAll(messages)
                    curModelName.value = c.model
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
                chatList.clear()
                chatList.addAll(repository.getAllChats())
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
        val chat = chatList[curChatIndex]
        updateChatSettings(chat, chatModel = modelsList[modelIndex].name)
        curModelName.value = modelsList[modelIndex].name
    }
}