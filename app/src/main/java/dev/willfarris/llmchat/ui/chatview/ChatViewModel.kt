package dev.willfarris.llmchat.ui.chatview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import dev.willfarris.llmchat.ChatAssistantApplication
import dev.willfarris.llmchat.data.ollama.ModelInfo
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
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
    var chatTitle: MutableState<String>,
    var chatPrompt: MutableState<String>,
    var chatContextSize: MutableState<String>,
    var chatModel: MutableState<String>,
)


class ChatViewModel(application: ChatAssistantApplication): ViewModel() {
    private val repository = application.chatAssistantRepository

    var messagesList = mutableStateListOf<ChatMessageUiContent>()
        private set
    var chatsList = mutableStateListOf<ChatSummaryUiContent>()
        private set
    var modelsList = mutableStateListOf<ModelInfo>()
        private set
    var curChatIndex: Int = OllamaPreferencesManager.lastChatIndex
        private set

    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val appLoadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)

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
                    repository.getAvailableModels().map { m ->
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                modelsList.add(m)
                            }
                        }
                    }
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

    fun errorMessageComplete() {
        errorMessage.update {
            null
        }
    }

    fun sendMessageFromUser(text: String) {
        val c = chatsList[curChatIndex]
        val newMessage = ChatMessageUiContent("user", mutableStateOf( text), c.chatModel.value)
        messagesList.add(newMessage)

        var assistantResponse: ChatMessageUiContent? = null
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    repository.sendMessage(c.chatId, c.chatModel.value, text).collect {
                        if(assistantResponse == null) {
                            assistantResponse = ChatMessageUiContent("assistant", mutableStateOf(""), c.chatModel.value)
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
        if(chatModel != null) c.chatModel.value = chatModel
        if(contextSize != null) c.chatContextSize.value = contextSize
        if(chatPrompt != null) c.chatPrompt.value = chatPrompt

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateChat(c)
            }
        }
    }

    fun selectChat(chatIndex: Int) {
        curChatIndex = chatIndex
        OllamaPreferencesManager.lastChatIndex = chatIndex
        messagesList.clear()
        val selectedChat = chatsList[curChatIndex]
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
                    selectChat(chatsList.lastIndex)
                }
            }
        }
    }
}