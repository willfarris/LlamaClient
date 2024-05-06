package dev.willfarris.llmchat.ui.chatview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import dev.willfarris.llmchat.ChatAssistantApplication
import dev.willfarris.llmchat.data.ollama.ModelInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.ConnectException
import java.util.UUID

data class ChatMessageUiContent(
    val role: String,
    val content: MutableState<String> = mutableStateOf(""),
    val modelName: String,
    val id: String = UUID.randomUUID().toString()
)

data class ChatSummaryUiContent(
    var chatId: Long,
    var chatName: MutableState<String>,
)

class ChatViewModel(application: ChatAssistantApplication): ViewModel() {
    private val repository = application.chatAssistantRepository

    var messagesList = mutableStateListOf<ChatMessageUiContent>()
    var chatsList = mutableStateListOf<ChatSummaryUiContent>()
    var modelsList = mutableStateListOf<ModelInfo>()
    var currentModel = mutableStateOf("")
    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    var curChatId: Long = -1

    val appLoadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getAllConversations().map { c ->
                    chatsList.add(c)
                }
                runBlocking {
                    if (chatsList.isEmpty()) {
                        createNewChat()
                    } else {
                        selectChat(chatsList.last().chatId)
                    }
                }
                try {
                    repository.getAvailableModels().map { m ->
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                modelsList.add(m)
                            }
                        }
                    }
                    currentModel.value = modelsList[0].name
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
    }

    fun chooseModel(index: Int) {
        currentModel.value = modelsList[index].name
    }

    fun errorMessageComplete() {
        errorMessage.update {
            null
        }
    }

    fun sendMessageFromUser(text: String) {
        if(curChatId == -1L) {
            createNewChat()
            Thread.sleep(100) // Oh dear this is dumb
        }
        val newMessage = ChatMessageUiContent("user", mutableStateOf( text), currentModel.value)
        messagesList.add(newMessage)

        var assistantResponse: ChatMessageUiContent? = null
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    repository.sendMessage(curChatId, currentModel.value, text).collect {
                        if(assistantResponse == null) {
                            assistantResponse = ChatMessageUiContent("assistant", mutableStateOf(""), currentModel.value)
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

    fun deleteChat(chatId: Long) {
        viewModelScope.launch {
            chatsList.removeAt(
                chatsList.indexOfFirst { c -> c.chatId == chatId }
            )
            withContext(Dispatchers.IO) {
                repository.deleteChatById(chatId)
            }
            if(chatId == curChatId) {
                if(chatsList.size > 0) {
                    selectChat(chatsList.last().chatId)
                } else {
                    createNewChat()
                }
            }
        }
    }

    fun updateChatName(chatId: Long, chatName: String) {
        val c = chatsList.find { c -> c.chatId == chatId }!!
        c.chatName = mutableStateOf(chatName)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateChat(c)
            }
        }
    }

    fun selectChat(chatId: Long) {
        if(chatId == curChatId) return

        curChatId = chatId
        messagesList.clear()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getAllMessages(curChatId).map { m ->
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
                val newChat = repository.createChat("New Chat", currentModel.value)
                withContext(Dispatchers.Main) {
                    chatsList.add(newChat)
                    selectChat(newChat.chatId)
                }
            }
        }
    }
}